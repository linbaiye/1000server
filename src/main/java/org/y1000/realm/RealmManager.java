package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.players.Player;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.realm.event.*;
import org.y1000.network.Connection;
import org.y1000.network.ConnectionEventType;
import org.y1000.network.event.ConnectionDataEvent;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.network.event.ConnectionEvent;
import org.y1000.repository.PlayerRepositoryImpl;
import org.y1000.sdb.MapSdb;

import java.util.*;
import java.util.concurrent.*;


@Slf4j
public final class RealmManager implements Runnable , CrossRealmEventSender {

    private final Map<Player, Integer> playerRealmMap;

    private final Map<Connection, Player> connectionPlayerMap;

    private final Map<String, Integer> playerNameRealmIdMap;

    private ExecutorService executorService;

    private final Queue<Object> eventQueue;

    private Map<Integer, RealmGroup> realmIdGroupMap;

    private final List<RealmGroup> groups;

    private volatile boolean shutdown;

    private RealmManager() {
        playerRealmMap = new ConcurrentHashMap<>();
        eventQueue = new ArrayDeque<>(100);
        connectionPlayerMap = new HashMap<>(500);
        shutdown = false;
        groups = new ArrayList<>();
        playerNameRealmIdMap = new HashMap<>();
    }


    public void startRealms() {
        groups.forEach(executorService::submit);
    }

    private int getPlayerLastRealm(Player player) {
        return PlayerRepositoryImpl.lastRealmId();
    }

    private void handleNewConnection(ConnectionEstablishedEvent event) {
        if (playerRealmMap.containsKey(event.player())) {
            // need to close current connection.
            log.error("Duplicate connection for {}.", event.player());
            event.connection().close();
            return;
        }
        var playerLastRealm = getPlayerLastRealm(event.player());
        RealmGroup group = realmIdGroupMap.get(playerLastRealm);
        if (group == null) {
            log.error("Realm {} does not exist.", playerLastRealm);
            event.connection().close();
            return;
        }
        connectionPlayerMap.put(event.connection(), event.player());
        playerRealmMap.put(event.player(), playerLastRealm);
        playerNameRealmIdMap.put(event.player().viewName(), playerLastRealm);
        group.handle(new ConnectionEstablishedEvent(playerLastRealm, event.player(), event.connection()));
    }


    private void sendDataToRealm(ConnectionDataEvent dataEvent) {
        Player player = connectionPlayerMap.get(dataEvent.connection());
        if (player == null) {
            log.warn("Close stray connection.");
            dataEvent.connection().close();
            return;
        }
        int realmId = playerRealmMap.get(player);
        ClientEvent data = dataEvent.data();
        realmIdGroupMap.get(realmId).handle(new PlayerDataEvent(realmId, player, data));
    }

    private void handleDisconnection(Connection connection) {
        Player removed = connectionPlayerMap.remove(connection);
        if (removed != null) {
            Integer realmId = playerRealmMap.remove(removed);
            realmIdGroupMap.get(realmId).handle(new PlayerDisconnectedEvent(realmId, removed));
            playerNameRealmIdMap.remove(removed.viewName());
        }
    }

    private void handle(ConnectionEvent event) {
        if (event.type() == ConnectionEventType.ESTABLISHED) {
            handleNewConnection((ConnectionEstablishedEvent)event);
        } else if (event.type() == ConnectionEventType.CLOSED) {
            handleDisconnection(event.connection());
        } else if (event.type() == ConnectionEventType.DATA) {
            sendDataToRealm((ConnectionDataEvent)event);
        }
    }

    private void handle(RealmEvent realmEvent) {
        if (realmEvent instanceof RealmTeleportEvent teleportEvent) {
            handleTeleport(teleportEvent);
        } else if (realmEvent instanceof BroadcastEvent) {
            groups.forEach(realmGroup -> realmGroup.handle(realmEvent));
        } else if (realmEvent instanceof RealmTriggerEvent realmTriggerEvent) {
            RealmGroup group = realmIdGroupMap.get(realmTriggerEvent.realmId());
            if (group != null) {
                group.handle(realmTriggerEvent);
            }
        } else if (realmEvent instanceof PrivateChatEvent privateChat) {
            Integer realm = playerNameRealmIdMap.get(privateChat.receiverName());
            if (realm == null) {
                if (playerNameRealmIdMap.containsKey(privateChat.senderName()))
                    handle(privateChat.noRecipient());
            } else {
                RealmGroup group = realmIdGroupMap.get(realm);
                if (group != null) {
                    group.handle(privateChat);
                }
            }
        }
    }

    public boolean shut() throws InterruptedException {
        executorService.shutdown();
        shutdown = true;
        return executorService.awaitTermination(60, TimeUnit.SECONDS);
    }

    public void queueEvent(Object event) {
        synchronized (eventQueue) {
            eventQueue.add(event);
            eventQueue.notifyAll();
        }
    }

    private void handleTeleport(RealmTeleportEvent teleportEvent) {
        int realmId = teleportEvent.realmId();
        playerRealmMap.remove(teleportEvent.player());
        playerRealmMap.put(teleportEvent.player(), realmId);
        playerNameRealmIdMap.put(teleportEvent.player().viewName(), realmId);
        RealmGroup group = realmIdGroupMap.get(realmId);
        group.handle(teleportEvent);
    }

    @Override
    public void send(RealmEvent realmEvent) {
        queueEvent(realmEvent);
    }

    private void setRealmGroups(List<RealmGroup> groups) {
        realmIdGroupMap = new ConcurrentHashMap<>();
        for (RealmGroup group : groups) {
            group.realmIds().forEach(id -> realmIdGroupMap.put(id,group));
        }
        this.executorService = Executors.newFixedThreadPool(groups.size());
        this.groups.addAll(groups);
    }

    private static final Set<Integer> IGNORED_REALMS = Set.of(31, 43, 46, 70, 71, 89);

    private static List<Integer> getRealmIds(MapSdb mapSdb) {
        var allIds = new ArrayList<>(mapSdb.getAllIds());
        allIds.removeAll(IGNORED_REALMS);
        return allIds;
        // return List.of(1, 3, 4, 19, 20, 49);
        //return List.of(19);
        //return List.of(49);
    }

    public static RealmManager create(MapSdb mapSdb, RealmFactory realmFactory) {

        List<Integer> realmIds = getRealmIds(mapSdb);
        List<Realm> realmList = new ArrayList<>();
        var manager = new RealmManager();
        for (Integer id : realmIds) {
            Realm realm = realmFactory.createRealm(id, manager);
            realmList.add(realm);
        }
        var groupSize = (realmList.size() / 4 ) > 0 ? (realmList.size() / 4) : 1;
        var left = realmList.size() % groupSize;
        int groupNumber = realmList.size() / groupSize + (left > 0 ? 1 : 0);
        List<RealmGroup> groups = new ArrayList<>();
        for (int i = 0, start = 0; i < groupNumber; i++, start += groupSize) {
            int end = Math.min(start + groupSize, realmList.size());
            RealmGroup group = new RealmGroup(realmList.subList(start, end), realmFactory, manager);
            groups.add(group);
        }
        manager.setRealmGroups(groups);
        return manager;
    }


    @Override
    public void run() {
        while (!shutdown) {
            try {
                Object event;
                synchronized (eventQueue) {
                    while (eventQueue.isEmpty()) {
                        eventQueue.wait();
                    }
                    event = eventQueue.poll();
                    eventQueue.notifyAll();
                }
                if (event instanceof ConnectionEvent connectionEvent)
                    handle(connectionEvent);
                else if (event instanceof RealmEvent realmEvent)
                    handle(realmEvent);
            } catch (Exception e) {
                log.error("Exception ", e);
            }
        }
    }
}
