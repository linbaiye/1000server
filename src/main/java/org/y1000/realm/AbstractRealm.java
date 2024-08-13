package org.y1000.realm;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.creatures.event.EntitySoundEvent;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.realm.event.*;
import org.y1000.sdb.MapSdb;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractRealm implements Realm {
    public static final int STEP_MILLIS = 10;
    private final RealmMap realmMap;
    private final RealmEntityEventSender eventSender;
    private final AbstractNpcManager npcManager;
    private final PlayerManager playerManager;
    private final DynamicObjectManager dynamicObjectManager;
    private final TeleportManager teleportManager;

    private final int id;
    private final RealmEventHandler crossRealmEventHandler;
    private final MapSdb mapSdb;
    private volatile boolean shutdown;
    private long accumulatedMillis;
    private final List<ActiveEntityManager<?>> entityManagers;

    public AbstractRealm(int id,
                         RealmMap realmMap,
                         RealmEntityEventSender eventSender,
                         GroundItemManager itemManager,
                         AbstractNpcManager npcManager,
                         PlayerManager playerManager,
                         DynamicObjectManager dynamicObjectManager,
                         TeleportManager teleportManager,
                         RealmEventHandler crossRealmEventHandler,
                         MapSdb mapSdb) {
        Validate.notNull(realmMap);
        Validate.notNull(eventSender);
        Validate.notNull(itemManager);
        Validate.notNull(playerManager);
        Validate.notNull(crossRealmEventHandler);
        Validate.notNull(mapSdb);
        this.realmMap = realmMap;
        this.eventSender = eventSender;
        this.npcManager = npcManager;
        this.playerManager = playerManager;
        this.dynamicObjectManager = dynamicObjectManager;
        this.teleportManager = teleportManager;
        this.id = id;
        this.crossRealmEventHandler = crossRealmEventHandler;
        this.mapSdb = mapSdb;
        this.entityManagers = new ArrayList<>();
        entityManagers.add(playerManager);
        entityManagers.add(itemManager);
        if (dynamicObjectManager != null)
            entityManagers.add(dynamicObjectManager);
        if (npcManager != null)
            entityManagers.add(npcManager);
    }

    public RealmMap map() {
        return realmMap;
    }

    public String name() {
        return mapSdb.getMapTitle(id);
    }

    public String bgm() {
        return mapSdb.getSoundBase(id);
    }

    void doUpdateEntities() {
        long current = System.currentTimeMillis();
        while (accumulatedMillis <= current) {
            entityManagers.forEach(m -> m.update(STEP_MILLIS));
            accumulatedMillis += STEP_MILLIS;
        }
    }


    abstract Logger log();

    public void init() {
        accumulatedMillis = System.currentTimeMillis();
        if (npcManager != null)
            npcManager.init();
        if (dynamicObjectManager != null)
            dynamicObjectManager.init();
        teleportManager.init(this::onPlayerTeleport);
        log().debug("Initialized {}.", this);
    }

    MapSdb getMapSdb() {
        return mapSdb;
    }

    public int id() {
        return id;
    }


    void onPlayerTeleport(PlayerRealmEvent event) {
        if (!(event instanceof RealmTeleportEvent realmTeleportEvent) ||
                !playerManager.contains(event.player())) {
            return;
        }
        playerManager.teleportOut(event.player());
        var connection = eventSender.remove(event.player());
        realmTeleportEvent.setConnection(connection);
        crossRealmEventHandler.handle(event);
    }

    RealmEventHandler getCrossRealmEventHandler() {
        return crossRealmEventHandler;
    }

    void acceptTeleport(RealmTeleportEvent teleportEvent) {
        eventSender.add(teleportEvent.player(), teleportEvent.getConnection());
        playerManager.teleportIn(teleportEvent.player(), this, teleportEvent.toCoordinate());
    }

    abstract void handleTeleportEvent(RealmTeleportEvent teleportEvent);

    PlayerManager playerManager() {
        return playerManager;
    }

    public void handle(RealmEvent event) {
        try {
            if (event instanceof ConnectionEstablishedEvent connectedEvent) {
                eventSender.add(connectedEvent.player(), connectedEvent.connection());
                playerManager.onPlayerConnected(connectedEvent.player(), this);
                log().debug("Added player to realm {}.", id);
            } else if (event instanceof PlayerDisconnectedEvent disconnectedEvent) {
                playerManager.onPlayerDisconnected(disconnectedEvent.player());
                eventSender.remove(disconnectedEvent.player());
            } else if (event instanceof PlayerDataEvent dataEvent) {
                playerManager.onClientEvent(dataEvent, npcManager);
            } else if (event instanceof RealmTeleportEvent teleportEvent) {
                handleTeleportEvent(teleportEvent);
            } else if (event instanceof BroadcastEvent broadcastEvent) {
                playerManager().allPlayers().forEach(broadcastEvent::send);
            } else if (event instanceof RealmLetterEvent<?> letterEvent) {
                npcManager.handleCrossRealmEvent(letterEvent);
            }
        } catch (Exception e) {
            log().error("Exception when handling event .", e);
        }
    }
}
