package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.players.Player;
import org.y1000.realm.event.*;
import org.y1000.network.Connection;
import org.y1000.network.ConnectionEventType;
import org.y1000.network.event.ConnectionDataEvent;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.network.event.ConnectionEvent;
import org.y1000.repository.PlayerRepositoryImpl;

import java.util.*;
import java.util.concurrent.*;


@Slf4j
public final class RealmManager implements Runnable , RealmEventHandler {

    private final Map<Player, Integer> playerRealmMap;

    private final Map<Connection, Player> connectionPlayerMap;

    private ExecutorService executorService;

    private final Queue<ConnectionEvent> eventQueue;

    private Map<Integer, RealmGroup> realmIdGroupMap;

    private final List<RealmGroup> groups;


    private volatile boolean shutdown;

    private RealmManager() {
        playerRealmMap = new ConcurrentHashMap<>();
        eventQueue = new ArrayDeque<>(100);
        connectionPlayerMap = new HashMap<>(500);
        shutdown = false;
        groups = new ArrayList<>();
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
        realmIdGroupMap.get(realmId).handle(new PlayerDataEvent(realmId, player, dataEvent.data()));
    }


    private void handleDisconnection(Connection connection) {
        Player removed = connectionPlayerMap.remove(connection);
        if (removed != null) {
            Integer realmId = playerRealmMap.remove(removed);
            realmIdGroupMap.get(realmId).handle(new PlayerDisconnectedEvent(realmId, removed));
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


    public boolean shut() throws InterruptedException {
        executorService.shutdown();
        shutdown = true;
        return executorService.awaitTermination(60, TimeUnit.SECONDS);
    }

    public void queueEvent(ConnectionEvent event) {
        synchronized (eventQueue) {
            eventQueue.add(event);
            eventQueue.notifyAll();
        }
    }


    private void handleTeleport(RealmTeleportEvent teleportEvent) {
        int realmId = teleportEvent.realmId();
        playerRealmMap.remove(teleportEvent.player());
        playerRealmMap.put(teleportEvent.player(), realmId);
        RealmGroup group = realmIdGroupMap.get(realmId);
        group.handle(teleportEvent);
    }

    @Override
    public void handle(RealmEvent realmEvent) {
        if (realmEvent instanceof RealmTeleportEvent teleportEvent) {
            handleTeleport(teleportEvent);
        } else if (realmEvent.realmEventType() == RealmEventType.BROADCAST) {
            groups.forEach(realmGroup -> realmGroup.handle(realmEvent));
        } else if (realmEvent instanceof RealmLetterEvent<?> realmLetterEvent) {
            RealmGroup group = realmIdGroupMap.get(realmLetterEvent.realmId());
            if (group != null) {
                log.debug("Sent to group with realm {}.",realmLetterEvent.realmId());
                group.handle(realmLetterEvent);
            } else {
                log.debug("No group to handle event.");
            }
        }
    }

    private void setRealmGroups(List<RealmGroup> groups) {
        realmIdGroupMap = new HashMap<>();
        for (RealmGroup group : groups) {
            group.realmIds().forEach(id -> realmIdGroupMap.put(id,group));
        }
        this.executorService = Executors.newFixedThreadPool(groups.size());
        this.groups.addAll(groups);
    }


    private static List<Integer> getRealmIds() {
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            result.add(i);
        }
        result.add(49);
        result.add(88);
        return result;
        // return List.of(1, 3, 4, 19, 20, 49);
        //return List.of(19);
        //return List.of(49);
    }

    public static RealmManager create(RealmFactory realmFactory) {

        List<Integer> realmIds = getRealmIds();
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
                ConnectionEvent event;
                synchronized (eventQueue) {
                    while (eventQueue.isEmpty()) {
                        eventQueue.wait();
                    }
                    event = eventQueue.poll();
                    eventQueue.notifyAll();
                }
                handle(event);
            } catch (Exception e) {
                log.error("Exception ", e);
            }
        }
    }
}
