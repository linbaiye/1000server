package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.y1000.account.AccountManager;
import org.y1000.account.AccountMessage;
import org.y1000.account.LoginCharacterRequest;
import org.y1000.input.*;
import org.y1000.network.ConnectionEvent;
import org.y1000.realm.event.*;
import org.y1000.network.Connection;
import org.y1000.network.ConnectionEventType;
import org.y1000.sdb.MapSdb;

import java.util.*;
import java.util.concurrent.*;


@Slf4j
public final class RealmManager implements Runnable , RealmEventSender {

    private ExecutorService executorService;

    private final BlockingQueue<ConnectionEvent> eventQueue;

    private volatile boolean shutdown;

    private final AccountManager accountManager;

    private final int realmNumber;

    private List<RealmGroup> realmGroups;

    private RealmManager(AccountManager accountManager, int realmNumber) {
        this.realmNumber = realmNumber;
        eventQueue = new ArrayBlockingQueue<>(100);
        shutdown = false;
        this.accountManager = accountManager;
    }

    public void startRealms() {
        realmGroups.forEach(executorService::submit);
    }


    private void logoutPlayer(long playerId) {
        realmGroups.forEach(r -> r.broadcast(Logout.byPlayerId(playerId)));
    }


    private void handleAccountMessage(Connection connection, AccountMessage message) {
        if (message instanceof LoginCharacterRequest characterRequest) {
            accountManager.getAllPlayerId(connection).forEach(this::logoutPlayer);
            long[] idAndRealmId = accountManager.loginCharacter(connection, characterRequest.name());
            if (idAndRealmId != null) {
                realmGroups.forEach(r -> r.handle((int) idAndRealmId[1], new Login(connection, idAndRealmId[0])));
            }
            else
                connection.tryClose();
        } else {
            accountManager.handle(connection, message);
        }
    }

    private void handleLogout(Connection co) {
        realmGroups.forEach(r -> r.broadcast(Logout.byConnection(co)));
    }

    private void handleDataEvent(Connection connection, Object data) {
        if (data instanceof AccountMessage accountMessage)
            handleAccountMessage(connection, accountMessage);
        else
            realmGroups.forEach(r -> r.broadcast(new ConnectionInput(connection, data)));
    }


    public void sendNotification(String text) {
        if (StringUtils.isEmpty(text))
            return;
        //realmIdGroupMap.values().forEach(groups -> groups.handle(notification));
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
            if (shutdown)
                return;
            shutdown = true;
            for (RealmGroup group : realmGroups) {
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
        if (event == null)
            return;
        try {
            boolean put = eventQueue.offer(event, 1, TimeUnit.SECONDS);
            if (!put)
                log.warn("Missing event {}.", event.data());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }


    private void setRealmGroups(List<RealmGroup> groups) {
        this.realmGroups = groups;
        this.executorService = Executors.newFixedThreadPool(groups.size());
    }

    private static final Set<Integer> IGNORED_REALMS = Set.of(31, 43, 46, 70, 71, 89);

    private static List<Integer> getRealmIds(MapSdb mapSdb) {
        var allIds = new ArrayList<>(mapSdb.getAllIds());
        allIds.removeAll(IGNORED_REALMS);
        return allIds;
    }

    private static final int DEFAULT_CORE_NUMBER = 4;

    public static RealmManager create(MapSdb mapSdb, RealmFactory realmFactory,
                                      AccountManager accountManager) {
        List<Integer> realmIds = getRealmIds(mapSdb);
        var groupSize = Math.min(realmIds.size(), DEFAULT_CORE_NUMBER);
        var groupMap = new HashMap<Integer, List<Realm>>(groupSize);
        var manager = new RealmManager(accountManager, realmIds.size());
        int idx = 0;
        for (Integer id : realmIds) {
            var list = groupMap.computeIfAbsent(idx++, i -> new ArrayList<>());
            Realm realm = realmFactory.createRealm(id, manager);
            list.add(realm);
            if (idx >= groupSize)
                idx = 0;
        }
        List<RealmGroup> groups = groupMap.values().stream()
                .map(l -> new RealmGroup(l, realmFactory, manager))
                .toList();
        manager.setRealmGroups(groups);
        return manager;
    }


    @Override
    public void run() {
        while (!shutdown) {
            try {
                ConnectionEvent event = eventQueue.poll(1, TimeUnit.SECONDS);
                if (event == null)
                    continue;
                if (event.type() == ConnectionEventType.DATA)
                    handleDataEvent(event.connection(), event.data());
                else if (event.type() == ConnectionEventType.CLOSED)
                    handleLogout(event.connection());
            } catch (Exception e) {
                log.error("Exception ", e);
            }
        }
    }

    private final Map<DeliveryPrivateChatEvent, Integer> privateChatReply = new HashMap<>();

    private void handlePrivateChatDelivery(DeliveryPrivateChatEvent event) {
        privateChatReply.put(event, realmNumber);
        realmGroups.forEach(r -> r.broadcast(event));
    }

    private void handlePrivateChatDeliveryResult(DeliveryPrivateChatResultEvent resultEvent) {
        Integer i = privateChatReply.get(resultEvent.source());
        if (i == null)
            return;
        i--;
        if (resultEvent.delivered() || i <= 0) {
            privateChatReply.remove(resultEvent.source());
            realmGroups.forEach(r -> r.broadcast(resultEvent));
        } else {
            privateChatReply.put(resultEvent.source(), i);
        }
    }

    @Override
    public void send(RealmEvent realmEvent) {
        if (realmEvent instanceof DeliveryPrivateChatEvent deliveryPrivateChatEvent) {
            handlePrivateChatDelivery(deliveryPrivateChatEvent);
        } else if (realmEvent instanceof DeliveryPrivateChatResultEvent deliveryPrivateChatResultEvent) {
            handlePrivateChatDeliveryResult(deliveryPrivateChatResultEvent);
        } else {
            realmGroups.forEach(r -> {
                if (realmEvent instanceof IdentifiedRealmEvent identifiedRealmEvent)
                    r.handle(identifiedRealmEvent.toRealm(), realmEvent);
                else
                    r.broadcast(realmEvent);
            });
        }
    }
}
