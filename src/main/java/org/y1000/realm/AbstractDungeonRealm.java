package org.y1000.realm;

import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.clientevent.ClientFoundGuildEvent;
import org.y1000.network.event.ConnectionEstablishedEvent;
import org.y1000.realm.event.PlayerDataEvent;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.sdb.MapSdb;
import org.y1000.util.Coordinate;


abstract class AbstractDungeonRealm extends AbstractRealm {

    private final int interval;
    private boolean closing;

    AbstractDungeonRealm(int id, RealmMap realmMap, RealmEntityEventSender eventSender,
                         GroundItemManager itemManager, NpcManager npcManager,
                         PlayerManager playerManager, DynamicObjectManager dynamicObjectManager,
                         TeleportManager teleportManager, CrossRealmEventSender crossRealmEventSender, MapSdb mapSdb,
                         ChatManager chatManager, int interval) {
        super(id, realmMap, eventSender, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb, chatManager);
        if (interval != 180000 && interval != 360000) {
            log().warn("Not a neat dungeon realm: {}.", id);
        }
        this.interval = interval;
        closing = false;
    }

    public boolean isHalfHourInterval() {
        return interval == 180000;
    }

    protected int exitRealmIt() {
        return getMapSdb().getTargetServerID(id());
    }

    protected Coordinate exitCoordinate() {
        return Coordinate.xy(getMapSdb().getTargetX(id()), getMapSdb().getTargetY(id()));
    }

    protected void teleportOut(Player player) {
        onPlayerTeleport(new RealmTeleportEvent(player, exitRealmIt(), exitCoordinate()));
    }

    boolean isClosing() {
        return closing;
    }

    @Override
    void handleConnectionEvent(ConnectionEstablishedEvent connectedEvent) {
        getEventSender().add(connectedEvent.player(), connectedEvent.connection());
        getPlayerManager().onPlayerConnected(connectedEvent.player(), this);
        teleportOut(connectedEvent.player());
    }

    @Override
    void handleGuidCreation(Player source, ClientFoundGuildEvent event) {
        source.emitEvent(PlayerTextEvent.forbidGuildCreation(source));
    }

    @Override
    void handleClientEvent(PlayerDataEvent dataEvent) {
        playerManager().onClientEvent(dataEvent, npcManager());
    }

    public void close() {
        if (closing) {
            return;
        }
        closing = true;
        playerManager().allPlayers().forEach(this::teleportOut);
    }

    @Override
    public void init() {
        doInit();
    }

    @Override
    public void update() {
        doUpdateEntities();
    }
}
