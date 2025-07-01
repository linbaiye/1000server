package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.y1000.account.AccountManager;
import org.y1000.message.input.*;
import org.y1000.network.ConnectionEvent;
import org.y1000.realm.event.*;
import org.y1000.network.Connection;
import org.y1000.network.ConnectionEventType;
import org.y1000.repository.PlayerRepository;
import org.y1000.sdb.MapSdb;

import java.util.*;
import java.util.concurrent.*;


@Slf4j
public final class RealmManager implements Runnable , CrossRealmEventSender {

    private final Map<String, Integer> playerNameRealmIdMap;

    private ExecutorService executorService;

    private final Queue<ConnectionEvent> eventQueue;

    private Map<Integer, RealmGroup> realmIdGroupMap;

    private volatile boolean shutdown;

    private final AccountManager accountManager;

    private final PlayerRepository playerRepository;


    private final Map<Connection, Long> connectedDebugPlayers;

//    private final List<Long> availableDebugPlayers = List.of(100000251L, 100000301L);
    private final List<Long> availableDebugPlayers = List.of(100000051L, 99999951L, 100000101L);

    private RealmManager(AccountManager accountManager,
                         PlayerRepository playerRepository) {
        eventQueue = new ArrayDeque<>(100);
        shutdown = false;
        playerNameRealmIdMap = new HashMap<>();
        this.playerRepository = playerRepository;
        this.accountManager = accountManager;
        connectedDebugPlayers = new HashMap<>();
    }

    public void startRealms() {
        realmIdGroupMap.values().forEach(executorService::submit);
    }


    private void handleLogin(Integer accountId, String charName, Connection connection) {
        playerRepository
                .findIdAndRealm(4, 100000301)
                .ifPresent(pair -> {
                    realmIdGroupMap.values().forEach(r -> r.broadcast(new Login(connection, pair.getLeft())));
                });
    }

    private Optional<Long> findAvailableDebugPlayer() {
        return availableDebugPlayers.stream().filter(playerId  -> !connectedDebugPlayers.containsValue(playerId)).findFirst();
    }


    private void handleDebug(Connection connection) {
        findAvailableDebugPlayer().ifPresentOrElse( playerId -> {
                    playerRepository.findRealm(playerId)
                        .ifPresent(realmId ->  {
                            realmIdGroupMap.values().forEach(r -> r.handle(realmId, new Login(connection, playerId)));
                            connectedDebugPlayers.put(connection, playerId);
                        });
                }, connection::tryClose);
    }

    private void handleLogout(Connection co) {
        connectedDebugPlayers.remove(co);
        realmIdGroupMap.values().forEach(r -> r.broadcast(new Logout(co)));
    }


    private void handleDataEvent(Connection connection, Object data) {
        if (data instanceof DebugInput) {
            handleDebug(connection);
        } else if (data instanceof SelfHandleInput input) {
            realmIdGroupMap.values().forEach(r -> r.broadcast(new ConnectionInput(connection, input)));
        }
    }

    /*private void handle(IConnectionEvent event) {
        if (event.type() == ConnectionEventType.CLOSED) {
            handleDisconnection(event.connection());
        } else if (event instanceof ConnectionDataEvent dataEvent) {
            if (dataEvent.data() instanceof LoginEvent loginEvent) {
                accountManager.removeToken(loginEvent.token())
                        .ifPresent(accountId -> handleLogin(accountId, loginEvent.charName(), dataEvent.connection()));
            }
            else {
                sendDataToRealm(dataEvent);
            }
        }
    }*/

//    private void handle(RealmEvent realmEvent) {
//        if (realmEvent instanceof RealmTeleportEvent teleportEvent) {
//            handleTeleport(teleportEvent);
//        } else if (realmEvent instanceof BroadcastEvent) {
//            realmIdGroupMap.values().forEach(realmGroup -> realmGroup.handle(realmEvent));
//        } else if (realmEvent instanceof RealmTriggerEvent realmTriggerEvent) {
//            RealmGroup group = realmIdGroupMap.get(realmTriggerEvent.toRealmId());
//            if (group != null) {
//                group.handle(realmTriggerEvent);
//            }
//        } else if (realmEvent instanceof PlayerWhisperEvent privateChat) {
//            Integer realm = playerNameRealmIdMap.get(privateChat.receiverName());
//            if (realm == null) {
//                if (playerNameRealmIdMap.containsKey(privateChat.senderName()))
//                    handle(privateChat.noRecipient());
//            } else {
//                RealmGroup group = realmIdGroupMap.get(realm);
//                if (group != null) {
//                    group.handle(privateChat);
//                }
//            }
//        }
//    }

    public void sendNotification(String text) {
        if (StringUtils.isEmpty(text))
            return;
        var notification = new SystemNotificationEvent(text);
        realmIdGroupMap.values().forEach(groups -> groups.handle(notification));
    }

    public synchronized void testKick() {
        /*for (Map.Entry<Integer, Player> accountPlayer : accountPlayerMap.entrySet()) {
            for (Map.Entry<Connection, Player> connectionPlayer : connectionPlayerMap.entrySet()) {
                if (connectionPlayer.getValue().equals(accountPlayer.getValue())) {
                    handleDisconnection(connectionPlayer.getKey());
                }
            }
        }
        accountPlayerMap.clear();*/
    }

    public synchronized void shut() {
        try {
            shutdown = true;
            if (shutdown)
                return;
            for (RealmGroup group : realmIdGroupMap.values()) {
                group.shutdown();
            }
            executorService.shutdown();
            executorService.awaitTermination(300, TimeUnit.SECONDS);
            log.info("All realms shutdown.");
        } catch (InterruptedException e) {
            log.error("Failed to shutdown.", e);
        }
    }

    public void queueEvent(ConnectionEvent event) {
        synchronized (eventQueue) {
            eventQueue.add(event);
            eventQueue.notifyAll();
        }
    }

    private void handleTeleport(RealmTeleportEvent teleportEvent) {
        int realmId = teleportEvent.toRealmId();
        //playerRealmMap.remove(teleportEvent.player());
        //playerRealmMap.put(teleportEvent.player(), realmId);
        playerNameRealmIdMap.put(teleportEvent.player().viewName(), realmId);
        RealmGroup group = realmIdGroupMap.get(realmId);
        log.debug("Teleporting player {} to realm {} from realm {}.", teleportEvent.player(), realmId, teleportEvent.fromRealmId());
        group.handle(teleportEvent);
    }

    @Override
    public void send(RealmEvent realmEvent) {
        //queueEvent(realmEvent);
    }


    private void setRealmGroups(List<RealmGroup> groups) {
        realmIdGroupMap = new ConcurrentHashMap<>();
        for (RealmGroup group : groups) {
            group.realmIds().forEach(id -> realmIdGroupMap.put(id,group));
        }
        this.executorService = Executors.newFixedThreadPool(groups.size());
    }

    private static final Set<Integer> IGNORED_REALMS = Set.of(31, 43, 46, 70, 71, 89);

    private static List<Integer> getRealmIds(MapSdb mapSdb) {
        var allIds = new ArrayList<>(mapSdb.getAllIds());
        allIds.removeAll(IGNORED_REALMS);
        return allIds;
    }

    public static RealmManager create(MapSdb mapSdb, RealmFactory realmFactory,
                                      AccountManager accountManager, PlayerRepository playerRepository) {
        List<Integer> realmIds = getRealmIds(mapSdb);
        List<Realm> realmList = new ArrayList<>();
        var manager = new RealmManager(accountManager, playerRepository);
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
                if (event.type() == ConnectionEventType.DATA)
                    handleDataEvent(event.connection(), event.data());
                else if (event.type() == ConnectionEventType.CLOSED)
                    handleLogout(event.connection());
            } catch (Exception e) {
                log.error("Exception ", e);
            }
        }
    }
}
