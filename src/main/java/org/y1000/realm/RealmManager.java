package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.monster.MonsterFactory;
import org.y1000.item.ItemFactory;
import org.y1000.entities.players.Player;
import org.y1000.item.ItemSdb;
import org.y1000.repository.ItemRepository;
import org.y1000.network.Connection;
import org.y1000.network.ConnectionEventType;
import org.y1000.network.event.ConnectionDataEvent;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.network.event.ConnectionEvent;
import org.y1000.realm.event.PlayerDataEvent;
import org.y1000.realm.event.PlayerDisconnectedEvent;
import org.y1000.sdb.MonstersSdb;

import java.util.*;
import java.util.concurrent.*;


@Slf4j
public final class RealmManager implements Runnable {
    private final Map<String, RealmImpl> realms;

    private final Map<Player, RealmImpl> playerRealmMap;

    private final Map<Connection, Player> connectionPlayerMap;

    private final ExecutorService executorService;

    private final Queue<ConnectionEvent> eventQueue;

    private volatile boolean shutdown;

    private RealmManager(Map<String, RealmImpl> realms) {
        this.realms = realms;
        executorService = Executors.newFixedThreadPool(realms.size());
        playerRealmMap = new HashMap<>();
        eventQueue = new ArrayDeque<>(100);
        connectionPlayerMap = new HashMap<>(500);
        shutdown = false;
    }

    public void startRealms() {
        realms.values().forEach(executorService::submit);
    }

    private String getPlayerLastRealm(Player player) {
        return "start";
    }

    private void handleNewConnection(ConnectionEstablishedEvent event) {
        if (playerRealmMap.containsKey(event.player())) {
            // need to close current connection.
            return;
        }
        String playerLastRealm = getPlayerLastRealm(event.player());
        RealmImpl realm = realms.get(playerLastRealm);
        if (realm == null) {
            log.error("Realm {} does not exist.", playerLastRealm);
            event.connection().close();
            return;
        }
        connectionPlayerMap.put(event.connection(), event.player());
        playerRealmMap.put(event.player(), realm);
        realm.handle(event);
    }

    private void sendDataToRealm(ConnectionDataEvent dataEvent) {
        Player player = connectionPlayerMap.get(dataEvent.connection());
        if (player == null) {
            log.warn("Close stray connection.");
            dataEvent.connection().close();
            return;
        }
        playerRealmMap.get(player).handle(new PlayerDataEvent(player, dataEvent.data()));
    }

    private void handleDisconnection(Connection connection) {
        Player removed = connectionPlayerMap.remove(connection);
        if (removed != null) {
            RealmImpl realm = playerRealmMap.remove(removed);
            realm.handle(new PlayerDisconnectedEvent(removed));
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

    public static RealmManager create(ItemFactory itemFactory,
                                      ItemRepository itemRepository,
                                      MonsterFactory monsterFactory,
                                      ItemSdb itemSdb,
                                      MonstersSdb monstersSdb) {
        Map<String, RealmImpl> realmMap = new HashMap<>();
        RealmImpl realm = RealmMap.Load("start")
                .map(m -> new RealmImpl(m, itemRepository, itemFactory, monsterFactory, itemSdb, monstersSdb))
                .orElseThrow(() -> new IllegalArgumentException("Map not found."));
        realmMap.put(realm.map().name(), realm);
        return new RealmManager(realmMap);
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
