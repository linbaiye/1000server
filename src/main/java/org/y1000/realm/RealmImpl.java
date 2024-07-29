package org.y1000.realm;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.npc.NpcFactory;
import org.y1000.entities.objects.DynamicObjectFactory;
import org.y1000.item.ItemFactory;
import org.y1000.item.ItemSdb;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.repository.ItemRepository;
import org.y1000.realm.event.PlayerDataEvent;
import org.y1000.realm.event.PlayerDisconnectedEvent;
import org.y1000.realm.event.RealmEvent;
import org.y1000.sdb.CreateEntitySdbRepository;
import org.y1000.sdb.CreateGateSdb;
import org.y1000.sdb.MapSdb;
import org.y1000.sdb.MonstersSdb;

import java.util.*;


@Slf4j
final class RealmImpl implements Realm {

    public static final int STEP_MILLIS = 10;

    private final RealmMap realmMap;

    private final List<RealmEvent> pendingEvents;

    private final RealmEntityEventSender eventSender;

    private volatile boolean shutdown;

    private final ItemManagerImpl itemManager;

    private final NpcManager npcManager;

    private final PlayerManager playerManager;

    private final DynamicObjectManagerImpl dynamicObjectManager;

    private final TeleportManager teleportManager;

    private long accumulatedMillis;

    private final int id;

    private final CrossRealmEventHandler crossRealmEventHandler;

    private final MapSdb mapSdb;

    public RealmImpl(RealmMap map,
                     ItemRepository itemRepository,
                     ItemFactory itemFactory,
                     NpcFactory npcFactory,
                     ItemSdb itemSdb,
                     MonstersSdb monstersSdb, int id,
                     CreateEntitySdbRepository createEntitySdbRepository,
                     DynamicObjectFactory dynamicObjectFactory,
                     CreateGateSdb createGateSdb,
                     CrossRealmEventHandler crossRealmEventHandler,
                     MapSdb mapSdb) {
        realmMap = map;
        this.id = id;
        this.mapSdb = mapSdb;
        var entityIdGenerator = new EntityIdGenerator();
        eventSender = new RealmEntityEventSender();
        itemManager = new ItemManagerImpl(eventSender, itemSdb, entityIdGenerator, itemFactory);
        npcManager = new NpcManager(eventSender, entityIdGenerator, npcFactory, itemManager, createEntitySdbRepository, monstersSdb);
        dynamicObjectManager = new DynamicObjectManagerImpl(dynamicObjectFactory, createEntitySdbRepository, entityIdGenerator, eventSender, itemManager, createEntitySdbRepository.loadObject(id));
        shutdown = false;
        pendingEvents = new ArrayList<>(100);
        this.playerManager = new PlayerManager(eventSender, itemManager, itemFactory, dynamicObjectManager);
        this.teleportManager = new TeleportManager(createGateSdb, entityIdGenerator);
        this.crossRealmEventHandler = crossRealmEventHandler;
    }

    public RealmMap map() {
        return realmMap;
    }

    @Override
    public String name() {
        return mapSdb.getMapTitle(id);
    }

    @Override
    public String bgm() {
        return mapSdb.getSoundBase(id);
    }

    @Override
    public void update() {
        long current = System.currentTimeMillis();
        while (accumulatedMillis <= current) {
            playerManager.update(STEP_MILLIS);
            npcManager.update(STEP_MILLIS);
            itemManager.update(STEP_MILLIS);
            dynamicObjectManager.update(STEP_MILLIS);
            accumulatedMillis += STEP_MILLIS;
        }
    }

    @Override
    public void init() {
        accumulatedMillis = System.currentTimeMillis();
        log.info("Initializing realm {}.", this.map().mapFile());
        npcManager.init(this.map(), id);
        dynamicObjectManager.init(this.map(), id);
        teleportManager.init(this.map(), id, this::onPlayerTeleport);
    }

    @Override
    public int id() {
        return id;
    }

    private void onPlayerTeleport(RealmEvent event) {
        if (!(event instanceof RealmTeleportEvent realmTeleportEvent)) {
            return;
        }
        playerManager.teleportOut(event.player());
        log.debug("Player {} left {}.", event.player(), id());
        var connection = eventSender.remove(event.player());
        realmTeleportEvent.setConnection(connection);
        crossRealmEventHandler.handle(event);
    }


    private void onRealmEvent(RealmEvent event) {
        try {
            if (event instanceof ConnectionEstablishedEvent connectedEvent) {
                eventSender.add(connectedEvent.player(), connectedEvent.connection());
                playerManager.onPlayerConnected(connectedEvent.player(), this);
            } else if (event instanceof PlayerDisconnectedEvent disconnectedEvent) {
                playerManager.onPlayerDisconnected(disconnectedEvent.player());
                eventSender.remove(event.player());
            } else if (event instanceof PlayerDataEvent dataEvent) {
                playerManager.onClientEvent(dataEvent, npcManager);
            } else if (event instanceof RealmTeleportEvent teleportEvent) {
                log.debug("Teleport player {} to {} at {}.", teleportEvent.player(), id, teleportEvent.toCoordinate());
                eventSender.add(teleportEvent.player(), teleportEvent.getConnection());
                playerManager.teleportIn(teleportEvent.player(), this, teleportEvent.toCoordinate());
            }
        } catch (Exception e) {
            log.error("Exception when handling event .", e);
        }
    }


    @Override
    public void handle(RealmEvent event) {
        onRealmEvent(event);
    }
}
