package org.y1000.realm;

import org.y1000.entities.players.Player;
import org.y1000.message.PlayerTextEvent;
import org.y1000.message.input.ClientFoundGuildEvent;
import org.y1000.network.Connection;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.repository.PlayerRepository;
import org.y1000.sdb.MapSdb;
import org.y1000.util.Coordinate;


abstract class AbstractDungeonRealm extends AbstractRealm {

    private final int interval;
    private boolean closing;

    AbstractDungeonRealm(int id, RealmMap realmMap,
                         GroundItemManager itemManager, NpcManager npcManager,
                         PlayerManager playerManager, DynamicObjectManager dynamicObjectManager,
                         TeleportManager teleportManager, RealmEventSender crossRealmEventSender, MapSdb mapSdb,
                         int interval, PlayerRepository playerRepository) {
        super(id, realmMap, itemManager, npcManager, playerManager, dynamicObjectManager, teleportManager, crossRealmEventSender, mapSdb,
                playerRepository);
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
        Connection connection = getPlayerManager().prepareTeleport(player);
        if (connection != null)
            getCrossRealmEventHandler().send(RealmTeleportEvent.teleportOut(player, exitRealmIt(), exitCoordinate(), connection));
    }

    boolean isClosing() {
        return closing;
    }

    @Override
    void handleGuildCreation(Player source, ClientFoundGuildEvent event) {
        source.emitEvent(PlayerTextEvent.forbidGuildCreation(source));
    }

    @Override
    public void shutdown() {
        playerManager().shutdown();
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

}
