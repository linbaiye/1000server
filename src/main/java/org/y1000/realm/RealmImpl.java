package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.event.PlayerShootEvent;
import org.y1000.entities.creatures.monster.MonsterFactory;
import org.y1000.entities.players.Player;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;
import org.y1000.message.serverevent.PlayerLeftEvent;
import org.y1000.repository.ItemRepository;
import org.y1000.realm.event.PlayerConnectedEvent;
import org.y1000.realm.event.PlayerDataEvent;
import org.y1000.realm.event.PlayerDisconnectedEvent;
import org.y1000.realm.event.RealmEvent;
import org.y1000.sdb.MonstersSdb;

import java.util.*;


@Slf4j
final class RealmImpl extends AbstractEntityManager<Player> implements Runnable, Realm, EntityEventListener {

    public static final int STEP_MILLIS = 10;

    private final RealmMap realmMap;

    private final List<RealmEvent> pendingEvents;

    private final RealmEntityEventSender eventSender;
    private volatile boolean shutdown;

    private final ItemManager itemManager;

    private final MonsterManager monsterManager;

    private final EntityIdGenerator entityIdGenerator;

    private final ProjectileManager projectileManager;

    public RealmImpl(RealmMap map,
                     ItemRepository itemRepository,
                     ItemFactory itemFactory,
                     MonsterFactory monsterFactory,
                     ItemSdb itemSdb,
                     MonstersSdb monstersSdb) {
        realmMap = map;
        entityIdGenerator = new EntityIdGenerator();
        eventSender = new RealmEntityEventSender(itemRepository, itemFactory);
        itemManager = new ItemManager(eventSender, itemSdb, monstersSdb, entityIdGenerator);
        monsterManager = new MonsterManager(eventSender, entityIdGenerator, monsterFactory);
        shutdown = false;
        pendingEvents = new ArrayList<>(100);
        projectileManager = new ProjectileManager();
    }

    public RealmMap map() {
        return realmMap;
    }


    private void onPlayerConnected(PlayerConnectedEvent connectedEvent) {
        if (eventSender.contains(connectedEvent.player())) {
            log.warn("Player {} already existed.", connectedEvent.player());
            connectedEvent.player().leaveRealm();
        }
        eventSender.add(connectedEvent.player(), connectedEvent.connection());
        add(connectedEvent.player());
    }

    private void onPlayerDisconnected(PlayerDisconnectedEvent disconnectedEvent) {
        disconnectedEvent.player().leaveRealm();
    }

    private void onRealmEvent(RealmEvent event) {
        try {
            if (event instanceof PlayerConnectedEvent connectedEvent) {
                onPlayerConnected(connectedEvent);
            } else if (event instanceof PlayerDisconnectedEvent disconnectedEvent) {
                onPlayerDisconnected(disconnectedEvent);
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
                    update(STEP_MILLIS);
                    monsterManager.update(STEP_MILLIS);
                    itemManager.update(STEP_MILLIS);
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
        monsterManager.init(this.map());
        startRealm();
    }

    @Override
    public Optional<Entity> findInsight(Entity source, long id) {
        return eventSender.findInsight(source, id);
    }

    @Override
    public void handle(RealmEvent event) {
        synchronized (pendingEvents) {
            pendingEvents.add(event);
            pendingEvents.notify();
        }
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    public void update(long delta) {
        updateManagedEntities(delta);
        projectileManager.update(delta);
    }

    @Override
    protected void onAdded(Player entity) {
        entity.joinReam(this);
        entity.registerEventListener(this);
    }

    @Override
    protected void onDeleted(Player entity) {
        entity.deregisterEventListener(this);
        eventSender.remove(entity);
    }

    @Override
    public void onEvent(EntityEvent entityEvent) {
        if (entityEvent instanceof PlayerLeftEvent playerLeftEvent) {
            delete(playerLeftEvent.player());
        } else if (entityEvent instanceof PlayerShootEvent shootEvent) {
            projectileManager.add(shootEvent.projectile());
        }
    }
}
