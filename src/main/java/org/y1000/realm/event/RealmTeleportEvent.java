package org.y1000.realm.event;

import lombok.Getter;
import lombok.Setter;
import org.y1000.entities.players.Player;
import org.y1000.network.Connection;
import org.y1000.util.Coordinate;

import java.util.Optional;

public final class RealmTeleportEvent implements PlayerRealmEvent {
    private final Player player;
    private final int realmId;
    private final Coordinate toCoordinate;

    private final int fromId;

    @Getter
    @Setter
    private Connection connection;

    private final Coordinate rejectCoordinate;

    public RealmTeleportEvent(Player player, int realmId, Coordinate toCoordinate) {
        this(player, realmId, toCoordinate, null, 0);
    }

    public RealmTeleportEvent(Player player, int realmId, Coordinate toCoordinate, int fromId) {
        this(player, realmId, toCoordinate, null, fromId);
    }

    public RealmTeleportEvent(Player player, int realmId, Coordinate toCoordinate, int fromId, Coordinate rejectCoordinate) {
        this(player, realmId, toCoordinate, null, fromId, rejectCoordinate);
    }


    public RealmTeleportEvent(Player player, int realmId, Coordinate toCoordinate,
                              Connection connection, int from) {
        this(player, realmId, toCoordinate, connection, from, null);
    }

    public RealmTeleportEvent(Player player, int realmId, Coordinate toCoordinate,
                              Connection connection, int from, Coordinate rejectCoordinate) {
        this.player = player;
        this.realmId = realmId;
        this.toCoordinate = toCoordinate;
        this.connection = connection;
        this.fromId = from;
        this.rejectCoordinate = rejectCoordinate;
    }

    @Override
    public Player player() {
        return player;
    }

    public Optional<Coordinate> rejectCoordinate() {
        return Optional.ofNullable(rejectCoordinate);
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
