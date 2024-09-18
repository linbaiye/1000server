package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.y1000.account.AccountManager;
import org.y1000.entities.players.Player;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.message.clientevent.LoginEvent;
import org.y1000.realm.event.*;
import org.y1000.network.Connection;
import org.y1000.network.ConnectionEventType;
import org.y1000.network.event.ConnectionDataEvent;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.network.event.ConnectionEvent;
import org.y1000.repository.PlayerRepository;
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

    private final AccountManager accountManager;

    private final PlayerRepository playerRepository;

    private final Map<Integer, Player> accountPlayerMap;


    private RealmManager(AccountManager accountManager,
                         PlayerRepository playerRepository) {
        playerRealmMap = new ConcurrentHashMap<>();
        eventQueue = new ArrayDeque<>(100);
        connectionPlayerMap = new HashMap<>(500);
        shutdown = false;
        groups = new ArrayList<>();
        playerNameRealmIdMap = new HashMap<>();
        accountPlayerMap = new HashMap<>();
        this.playerRepository = playerRepository;
        this.accountManager = accountManager;
    }

    public void startRealms() {
        groups.forEach(executorService::submit);
    }

    private void loginToRealm(Player player, int realmId, Connection connection) {
        if (playerRealmMap.containsKey(player)) {
            // need to close current connection.
            log.error("Duplicate connection for {}.", player);
            connection.close();
            return;
        }
        RealmGroup group = realmIdGroupMap.get(realmId);
        if (group == null) {
            log.error("Realm {} does not exist.", realmId);
            connection.close();
            return;
        }
        connectionPlayerMap.put(connection, player);
        playerRealmMap.put(player, realmId);
        playerNameRealmIdMap.put(player.viewName(), realmId);
        group.handle(new ConnectionEstablishedEvent(realmId, player, connection));
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
        if (removed == null) {
            return;
        }
        Integer realmId = playerRealmMap.remove(removed);
        realmIdGroupMap.get(realmId).handle(new PlayerDisconnectedEvent(realmId, removed));
        playerNameRealmIdMap.remove(removed.viewName());
        Integer found = null;
        for (var accountId : accountPlayerMap.keySet()) {
            if (accountPlayerMap.get(accountId).equals(removed)) {
                found = accountId;
                break;
            }
        }
        if (found != null) {
            accountPlayerMap.remove(found);
        }
    }

    private void handleLogin(Integer accountId, String charName, Connection connection) {
        Player currentLogged = accountPlayerMap.remove(accountId);
        if (currentLogged != null) {
            for (var kv : connectionPlayerMap.entrySet()) {
                if (kv.getValue().equals(currentLogged)) {
                    log.info("Kick player {} of account id {}.", currentLogged.viewName(), accountId);
                    handleDisconnection(kv.getKey());
                    break;
                }
            }
            connection.close();
        } else {
            playerRepository.find(accountId, charName)
                    .ifPresent(pair -> {
                        accountPlayerMap.put(accountId, pair.getLeft());
                        loginToRealm(pair.getLeft(), pair.getRight(), connection);
                    });
        }
    }

    private void handle(ConnectionEvent event) {
        if (event.type() == ConnectionEventType.CLOSED) {
            handleDisconnection(event.connection());
        } else if (event instanceof ConnectionDataEvent dataEvent) {
            if (dataEvent.data() instanceof LoginEvent loginEvent) {
                accountManager.removeToken(loginEvent.token())
                        .ifPresent(accountId -> handleLogin(accountId, loginEvent.charName(), dataEvent.connection()));
            } else {
                sendDataToRealm(dataEvent);
            }
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
        } else if (realmEvent instanceof PlayerWhisperEvent privateChat) {
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

    public void sendNotification(String text) {
        if (StringUtils.isEmpty(text))
            return;
        var notification = new SystemNotificationEvent(text);
        groups.forEach(groups -> groups.handle(notification));
    }

    public void shut() {
        try {
            shutdown = true;
            groups.forEach(RealmGroup::shutdown);
            executorService.shutdown();
            executorService.awaitTermination(300, TimeUnit.SECONDS);
            log.info("All realms shutdown.");
        } catch (InterruptedException e) {
            log.error("Failed to shutdown.", e);
        }
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
