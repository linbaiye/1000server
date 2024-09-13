package org.y1000.realm;

import org.y1000.entities.creatures.State;
import org.y1000.entities.players.Player;
import org.y1000.realm.event.RealmTeleportEvent;
import org.y1000.util.Coordinate;
import org.y1000.util.UnaryAction;

import java.util.Set;

final class DeadPlayerTeleportManagerImpl implements DeadPlayerTeleportManager {
    private final int toRealmId;

    private final Coordinate toCoordinate;

    private final EntityTimerManager<Player> timerManager;

    private UnaryAction<RealmTeleportEvent> teleportHandler;

    public DeadPlayerTeleportManagerImpl(int toRealmId,
                                         Coordinate toCoordinate) {
        this.toRealmId = toRealmId;
        this.toCoordinate = toCoordinate;
        this.timerManager = new EntityTimerManager<>();
    }


    @Override
    public void setTeleportHandler(UnaryAction<RealmTeleportEvent> teleportHandler) {
        this.teleportHandler = teleportHandler;
    }

    @Override
    public void onPlayerDead(Player player) {
        if (teleportHandler == null || player == null
                || player.stateEnum() != State.DIE) {
            return;
        }
        timerManager.add(player, 10000);
    }

    @Override
    public void update(long delta) {
        Set<Player> players = timerManager.update(delta);
        players.forEach(player -> teleportHandler.invoke(new RealmTeleportEvent(player, toRealmId, toCoordinate)));
    }
}
