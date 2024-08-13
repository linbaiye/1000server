package org.y1000.realm.event;

import lombok.Getter;
import lombok.Setter;
import org.y1000.entities.players.Player;
import org.y1000.network.Connection;
import org.y1000.util.Coordinate;

public final class RealmTeleportEvent implements PlayerRealmEvent {
    private final Player player;
    private final int realmId;
    private final Coordinate toCoordinate;

    @Getter
    @Setter
    private Connection connection;

    public RealmTeleportEvent(Player player, int realmId, Coordinate toCoordinate) {
        this(player, realmId, toCoordinate, null);
    }

    public RealmTeleportEvent(Player player, int realmId, Coordinate toCoordinate, Connection connection) {
        this.player = player;
        this.realmId = realmId;
        this.toCoordinate = toCoordinate;
        this.connection = connection;
    }

    @Override
    public Player player() {
        return player;
    }

    public Coordinate toCoordinate() {
        return toCoordinate;
    }

    @Override
    public int realmId() {
        return realmId;
    }
}
