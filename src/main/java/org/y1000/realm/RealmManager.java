package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.item.ItemFactory;
import org.y1000.entities.players.Player;
import org.y1000.item.ItemSdb;
import org.y1000.realm.event.RealmEvent;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.repository.ItemRepository;
import org.y1000.network.Connection;
import org.y1000.network.ConnectionEventType;
import org.y1000.network.event.ConnectionDataEvent;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.network.event.ConnectionEvent;
import org.y1000.realm.event.PlayerDataEvent;
import org.y1000.realm.event.PlayerDisconnectedEvent;
import org.y1000.repository.PlayerRepositoryImpl;
import org.y1000.sdb.CreateEntitySdbRepository;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.sdb.MapSdb;
import org.y1000.sdb.MonstersSdb;

import java.util.*;
import java.util.concurrent.*;


@Slf4j
public final class RealmManager implements Runnable , CrossRealmEventHandler{

    private final Map<Player, Integer> playerRealmMap;

    private final Map<Connection, Player> connectionPlayerMap;

    private ExecutorService executorService;

    private final Queue<ConnectionEvent> eventQueue;

    private Map<Integer, RealmGroup> realmGroups;

    private volatile boolean shutdown;

    private RealmManager() {
        playerRealmMap = new ConcurrentHashMap<>();
        eventQueue = new ArrayDeque<>(100);
        connectionPlayerMap = new HashMap<>(500);
        shutdown = false;
    }


    public void startRealms() {
        realmGroups.values().forEach(executorService::submit);
    }

    private int getPlayerLastRealm(Player player) {
        return PlayerRepositoryImpl.lastRealmId();
    }

    private void handleNewConnection(ConnectionEstablishedEvent event) {
        if (playerRealmMap.containsKey(event.player())) {
            // need to close current connection.
            return;
        }
        var playerLastRealm = getPlayerLastRealm(event.player());
        RealmGroup group = realmGroups.get(playerLastRealm);
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
        realmGroups.get(realmId).handle(new PlayerDataEvent(realmId, player, dataEvent.data()));
    }



    private void handleDisconnection(Connection connection) {
        Player removed = connectionPlayerMap.remove(connection);
        if (removed != null) {
            Integer realmId = playerRealmMap.remove(removed);
            realmGroups.get(realmId).handle(new PlayerDisconnectedEvent(realmId, removed));
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
        RealmGroup group = realmGroups.get(realmId);
        group.handle(teleportEvent);
    }

    @Override
    public void handle(RealmEvent realmEvent) {
        if (realmEvent instanceof RealmTeleportEvent teleportEvent) {
            handleTeleport(teleportEvent);
        }
    }

    private void setRealmGroups(Map<Integer, RealmGroup> realmGroups) {
        this.realmGroups = realmGroups;
        this.executorService = Executors.newFixedThreadPool(this.realmGroups.size());
    }


    private static List<Integer> getRealmIds() {
        return List.of(1, 19, 20, 49);
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
        List<RealmGroup> groups = new ArrayList<>();
        for (int i = 0, start = 0; i < realmList.size() / groupSize; i++, start += groupSize) {
            int end = start + groupSize;
            if (i == groupSize - 1) {
                end += left;
            }
            RealmGroup group = new RealmGroup(realmList.subList(start, end));
            groups.add(group);
        }
        Map<Integer, RealmGroup> realmGroupMap = new HashMap<>();
        for (RealmGroup group : groups) {
            group.realmIds().forEach(id -> realmGroupMap.put(id,group));
        }
        manager.setRealmGroups(realmGroupMap);
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
