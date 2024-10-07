package org.y1000.realm.event;

import lombok.Getter;
import lombok.Setter;
import org.y1000.entities.players.Player;
import org.y1000.entities.teleport.TeleportCost;
import org.y1000.network.Connection;
import org.y1000.util.Coordinate;

import java.util.Collections;
import java.util.List;
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

    @Getter
    private final List<TeleportCost> costs;

    public RealmTeleportEvent(Player player, int realmId, Coordinate toCoordinate) {
        this(player, realmId, toCoordinate, null, 0);
    }

    public RealmTeleportEvent(Player player, int realmId, Coordinate toCoordinate, int fromId, Coordinate rejectCoordinate, List<TeleportCost> costs) {
        this(player, realmId, toCoordinate, null, fromId, rejectCoordinate, costs);
    }

    public RealmTeleportEvent(Player player, int realmId, Coordinate toCoordinate,
                              Connection connection, int from) {
        this(player, realmId, toCoordinate, connection, from, null, null);
    }


    public RealmTeleportEvent(Player player, int realmId,
                              Coordinate toCoordinate,
                              Connection connection,
                              int from,
                              Coordinate rejectCoordinate,
                              List<TeleportCost> costs) {
        this.player = player;
        this.realmId = realmId;
        this.toCoordinate = toCoordinate;
        this.connection = connection;
        this.fromId = from;
        this.rejectCoordinate = rejectCoordinate;
        this.costs = costs != null ? costs : Collections.emptyList();
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

    public Optional<Coordinate> rejectCoordinate() {
        return Optional.ofNullable(rejectCoordinate);
    }

    public Optional<RealmTeleportEvent> rejectEvent() {
        if (rejectCoordinate != null)
            return Optional.of(new RealmTeleportEvent(player, fromId, rejectCoordinate, connection, realmId));
        return Optional.empty();
    }

    public String checkCost() {
        for (TeleportCost cost : costs) {
            var ret = cost.check(player);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    @Override
    public int toRealmId() {
        return realmId;
    }
}
