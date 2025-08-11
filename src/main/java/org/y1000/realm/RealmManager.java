package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.y1000.account.AccountManager;
import org.y1000.message.account.AccountMessage;
import org.y1000.message.account.LoginCharacterRequest;
import org.y1000.message.input.*;
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

    private final Queue<ConnectionEvent> eventQueue;

    private final Map<Integer, RealmGroup> realmIdGroupMap;

    private volatile boolean shutdown;

    private final AccountManager accountManager;

    private final int realmNumber;

    private RealmManager(AccountManager accountManager, int realmNumber) {
        this.realmNumber = realmNumber;
        eventQueue = new ArrayDeque<>(100);
        shutdown = false;
        this.accountManager = accountManager;
        realmIdGroupMap = new ConcurrentHashMap<>();
    }

    public void startRealms() {
        realmIdGroupMap.values().forEach(executorService::submit);
    }


    private void logoutPlayer(long playerId) {
        realmIdGroupMap.values().forEach(r -> r.broadcast(Logout.byPlayerId(playerId)));
    }


    private void handleAccountMessage(Connection connection, AccountMessage message) {
        if (message instanceof LoginCharacterRequest characterRequest) {
            accountManager.getAllPlayerId(connection).forEach(this::logoutPlayer);
            long[] idAndRealmId = accountManager.loginCharacter(connection, characterRequest.name());
            if (idAndRealmId != null) {
                realmIdGroupMap.values().forEach(r -> r.handle((int) idAndRealmId[1], new Login(connection, idAndRealmId[0])));
            }
            else
                connection.tryClose();
        } else {
            accountManager.handle(connection, message);
        }
    }

    private void handleLogout(Connection co) {
        realmIdGroupMap.values().forEach(r -> r.broadcast(Logout.byConnection(co)));
    }

    private void handleDataEvent(Connection connection, Object data) {
        if (data instanceof AccountMessage accountMessage)
            handleAccountMessage(connection, accountMessage);
        else
            realmIdGroupMap.values().forEach(r -> r.broadcast(new ConnectionInput(connection, data)));
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


    private void setRealmGroups(List<RealmGroup> groups) {
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
                                      AccountManager accountManager) {
        List<Integer> realmIds = getRealmIds(mapSdb);
        List<Realm> realmList = new ArrayList<>();
        var manager = new RealmManager(accountManager, realmIds.size());
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

    private final Map<DeliveryPrivateChatEvent, Integer> privateChatReply = new HashMap<>();

    private void handlePrivateChatDelivery(DeliveryPrivateChatEvent event) {
        privateChatReply.put(event, realmNumber);
        realmIdGroupMap.values().forEach(r -> r.broadcast(event));
    }

    private void handlePrivateChatDeliveryResult(DeliveryPrivateChatResultEvent resultEvent) {
        Integer i = privateChatReply.get(resultEvent.source());
        if (i == null)
            return;
        i--;
        if (resultEvent.delivered() || i <= 0) {
            privateChatReply.remove(resultEvent.source());
            realmIdGroupMap.values().forEach(r -> r.broadcast(resultEvent));
        } else {
            privateChatReply.put(resultEvent.source(), i);
        }
    }

    @Override
    public void send(RealmEvent realmEvent) {
        synchronized (realmIdGroupMap) {
            if (realmEvent instanceof DeliveryPrivateChatEvent deliveryPrivateChatEvent) {
                handlePrivateChatDelivery(deliveryPrivateChatEvent);
            } else if (realmEvent instanceof DeliveryPrivateChatResultEvent deliveryPrivateChatResultEvent) {
                handlePrivateChatDeliveryResult(deliveryPrivateChatResultEvent);
            } else {
                realmIdGroupMap.values().forEach(r -> {
                    if (realmEvent instanceof IdentifiedRealmEvent identifiedRealmEvent)
                        r.handle(identifiedRealmEvent.toRealm(), realmEvent);
                    else
                        r.broadcast(realmEvent);
                });
            }
        }
    }
}
