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

    private final int fromId;

    @Getter
    @Setter
    private Connection connection;

    public RealmTeleportEvent(Player player, int realmId, Coordinate toCoordinate) {
        this(player, realmId, toCoordinate, null, 0);
    }

    public RealmTeleportEvent(Player player, int realmId, Coordinate toCoordinate, int fromId) {
        this(player, realmId, toCoordinate, null, fromId);
    }

    public RealmTeleportEvent(Player player, int realmId, Coordinate toCoordinate,
                              Connection connection, int from) {
        this.player = player;
        this.realmId = realmId;
        this.toCoordinate = toCoordinate;
        this.connection = connection;
        this.fromId = from;
    }

    @Override
    public Player player() {
        return player;
    }

    public Coordinate toCoordinate() {
        return toCoordinate;
    }

    public int fromRealmId() {
        return fromId;
    }

    @Override
    public int realmId() {
        return realmId;
    }
}
