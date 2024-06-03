package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.item.ItemFactory;
import org.y1000.repository.ItemRepository;
import org.y1000.realm.event.PlayerConnectedEvent;
import org.y1000.realm.event.PlayerDataEvent;
import org.y1000.realm.event.PlayerDisconnectedEvent;
import org.y1000.realm.event.RealmEvent;
import org.y1000.util.Coordinate;

import java.util.*;


@Slf4j
final class RealmImpl implements Runnable, Realm {

    public static final int STEP_MILLIS = 10;

    private final RealmMap realmMap;

    private final List<RealmEvent> pendingEvents;

    private final RealmEntityManager entityManager;
    private volatile boolean shutdown;

    public RealmImpl(RealmMap map,
                     ItemRepository itemRepository,
                     ItemFactory itemFactory) {
        realmMap = map;
        entityManager = new RealmEntityManager(itemRepository, itemFactory);
        shutdown = false;
        pendingEvents = new ArrayList<>(100);
    }

    public RealmMap map() {
        return realmMap;
    }

    private void initEntities() {
        List<PassiveMonster> monsters = List.of(new PassiveMonster(1000L, new Coordinate(39, 30), Direction.DOWN, "牛", realmMap));
        monsters.forEach(entityManager::add);
    }


    private void dispatchEvent(RealmEvent event) {
        try {
            if (event instanceof PlayerConnectedEvent connectedEvent) {
                if (entityManager.contains(connectedEvent.player())) {
                    log.warn("Player {} already existed.", connectedEvent.player());
                    connectedEvent.player().leaveRealm();
                    entityManager.remove(event.player());
                }
                entityManager.add(connectedEvent.player(), connectedEvent.connection());
                connectedEvent.player().joinReam(this);
            } else if (event instanceof PlayerDisconnectedEvent disconnectedEvent) {
                disconnectedEvent.player().leaveRealm();
            } else if (event instanceof PlayerDataEvent dataEvent) {
                dataEvent.player().handleClientEvent(dataEvent.data());
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
                    entityManager.updateEntities(STEP_MILLIS);
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
                events.forEach(this::dispatchEvent);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        initEntities();
        startRealm();
    }

    @Override
    public Optional<PhysicalEntity> findInsight(PhysicalEntity source, long id) {
        return entityManager.findInsight(source, id);
    }

    @Override
    public void handle(RealmEvent event) {
        synchronized (pendingEvents) {
            pendingEvents.add(event);
            pendingEvents.notify();
        }
    }
}
