package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.repository.ItemRepository;
import org.y1000.realm.event.PlayerConnectedEvent;
import org.y1000.realm.event.PlayerDataEvent;
import org.y1000.realm.event.PlayerDisconnectedEvent;
import org.y1000.realm.event.RealmEvent;
import org.y1000.sdb.CreateEntitySdbRepository;
import org.y1000.sdb.MonstersSdb;

import java.util.*;


@Slf4j
final class RealmImpl implements Runnable, Realm {

    public static final int STEP_MILLIS = 10;

    private final RealmMap realmMap;

    private final List<RealmEvent> pendingEvents;

    private final RealmEntityEventSender eventSender;

    private volatile boolean shutdown;

    private final ItemManagerImpl itemManager;

    private final NpcManager npcManager;

    private final PlayerManager playerManager;

    private final DynamicObjectManagerImpl dynamicObjectManager;

    private final int id;

    public RealmImpl(RealmMap map,
                     ItemRepository itemRepository,
                     ItemFactory itemFactory,
                     NpcFactory npcFactory,
                     ItemSdb itemSdb,
                     MonstersSdb monstersSdb, int id,
                     CreateEntitySdbRepository createEntitySdbRepository,
                     DynamicObjectFactory dynamicObjectFactory) {
        realmMap = map;
        this.id = id;
        var entityIdGenerator = new EntityIdGenerator();
        eventSender = new RealmEntityEventSender();
        itemManager = new ItemManagerImpl(eventSender, itemSdb, monstersSdb, entityIdGenerator, itemFactory);
        npcManager = new NpcManager(eventSender, entityIdGenerator, npcFactory, itemManager, createEntitySdbRepository);
        dynamicObjectManager = new DynamicObjectManagerImpl(dynamicObjectFactory, createEntitySdbRepository, entityIdGenerator, eventSender);
        shutdown = false;
        pendingEvents = new ArrayList<>(100);
        this.playerManager = new PlayerManager(eventSender, itemManager, itemFactory, dynamicObjectManager);
    }

    public RealmMap map() {
        return realmMap;
    }

    @Override
    public int id() {
        return 0;
    }


    private void onRealmEvent(RealmEvent event) {
        try {
            if (event instanceof PlayerConnectedEvent connectedEvent) {
                playerManager.onPlayerConnected(connectedEvent.player(), connectedEvent.connection(), this);
            } else if (event instanceof PlayerDisconnectedEvent disconnectedEvent) {
                playerManager.onPlayerDisconnected(disconnectedEvent.player());
            } else if (event instanceof PlayerDataEvent dataEvent) {
                playerManager.onPlayerEvent(dataEvent, npcManager);
            }
        } catch (Exception e) {
            log.error("Exception when handling event .", e);
        }
    }

    private void startRealm() {
        long accumulatedMillis = System.currentTimeMillis();
        try {
            while (!shutdown) {
                long current = System.currentTimeMillis();
                if (accumulatedMillis <= current) {
                    playerManager.update(STEP_MILLIS);
                    npcManager.update(STEP_MILLIS);
                    itemManager.update(STEP_MILLIS);
                    dynamicObjectManager.update(STEP_MILLIS);
                    accumulatedMillis += STEP_MILLIS;
                    continue;
                }
                List<RealmEvent> events = Collections.emptyList();
                synchronized (pendingEvents) {
                    while (pendingEvents.isEmpty() && current < accumulatedMillis) {
                        pendingEvents.wait(accumulatedMillis - current);
                        current = System.currentTimeMillis();
                    }
                    if (!pendingEvents.isEmpty()) {
                        events = new ArrayList<>(pendingEvents);
                        pendingEvents.clear();
                    }
                    pendingEvents.notify();
                }
                events.forEach(this::onRealmEvent);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Exception ", e);
        }
    }

    @Override
    public void run() {
        try {
            npcManager.init(this.map(), id);
            dynamicObjectManager.init(this.map(), id);
            startRealm();
        } catch (Exception e) {
            log.error("Failed to start realm {}.", id, e);
        }
    }

    @Override
    public void handle(RealmEvent event) {
        synchronized (pendingEvents) {
            pendingEvents.add(event);
            pendingEvents.notify();
        }
    }
}
