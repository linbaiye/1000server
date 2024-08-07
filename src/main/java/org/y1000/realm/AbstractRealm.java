package org.y1000.realm;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.realm.event.PlayerDataEvent;
import org.y1000.realm.event.PlayerDisconnectedEvent;
import org.y1000.realm.event.RealmEvent;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.sdb.MapSdb;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractRealm implements Realm {
    public static final int STEP_MILLIS = 10;
    protected final RealmMap realmMap;
    protected final RealmEntityEventSender eventSender;
    protected final ItemManagerImpl itemManager;
    protected final AbstractNpcManager npcManager;
    protected final PlayerManager playerManager;
    protected final DynamicObjectManager dynamicObjectManager;
    protected final TeleportManager teleportManager;

    protected final int id;
    protected final CrossRealmEventHandler crossRealmEventHandler;
    protected final MapSdb mapSdb;
    protected volatile boolean shutdown;
    private long accumulatedMillis;
    private final List<ActiveEntityManager<?>> entityManagers;

    public AbstractRealm(int id,
                         RealmMap realmMap,
                         RealmEntityEventSender eventSender,
                         ItemManagerImpl itemManager,
                         AbstractNpcManager npcManager,
                         PlayerManager playerManager,
                         DynamicObjectManager dynamicObjectManager,
                         TeleportManager teleportManager,
                         CrossRealmEventHandler crossRealmEventHandler,
                         MapSdb mapSdb) {
        Validate.notNull(realmMap);
        Validate.notNull(eventSender);
        Validate.notNull(itemManager);
        Validate.notNull(playerManager);
        Validate.notNull(crossRealmEventHandler);
        Validate.notNull(mapSdb);
        this.realmMap = realmMap;
        this.eventSender = eventSender;
        this.itemManager = itemManager;
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

    public void update() {
        long current = System.currentTimeMillis();
        while (accumulatedMillis <= current) {
            entityManagers.forEach(m -> m.update(STEP_MILLIS));
            accumulatedMillis += STEP_MILLIS;
        }
    }

    protected abstract Logger log();

    public void init() {
        accumulatedMillis = System.currentTimeMillis();
        if (npcManager != null)
            npcManager.init();
        if (dynamicObjectManager != null)
            dynamicObjectManager.init(this.map());
        teleportManager.init(this.map(), id, this::onPlayerTeleport);
    }

    public int id() {
        return id;
    }

    private void onPlayerTeleport(RealmEvent event) {
        if (!(event instanceof RealmTeleportEvent realmTeleportEvent)) {
            return;
        }
        playerManager.teleportOut(event.player());
        var connection = eventSender.remove(event.player());
        realmTeleportEvent.setConnection(connection);
        crossRealmEventHandler.handle(event);
    }

    public void handle(RealmEvent event) {
        try {
            if (event instanceof ConnectionEstablishedEvent connectedEvent) {
                eventSender.add(connectedEvent.player(), connectedEvent.connection());
                playerManager.onPlayerConnected(connectedEvent.player(), this);
                log().debug("Added player to realm {}.", id);
            } else if (event instanceof PlayerDisconnectedEvent disconnectedEvent) {
                playerManager.onPlayerDisconnected(disconnectedEvent.player());
                eventSender.remove(event.player());
            } else if (event instanceof PlayerDataEvent dataEvent) {
                playerManager.onClientEvent(dataEvent, npcManager);
            } else if (event instanceof RealmTeleportEvent teleportEvent) {
                eventSender.add(teleportEvent.player(), teleportEvent.getConnection());
                playerManager.teleportIn(teleportEvent.player(), this, teleportEvent.toCoordinate());
            }
        } catch (Exception e) {
            log().error("Exception when handling event .", e);
        }
    }
}
