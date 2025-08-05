package org.y1000.realm.event;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.players.Player;
import org.y1000.network.Connection;
import org.y1000.util.Coordinate;

public final class RealmTeleportEvent implements IdentifiedRealmEvent {
    private final Player player;
    private final int realmId;
    private final Coordinate toCoordinate;
    private final Connection connection;

    public RealmTeleportEvent(Player player, int realmId,
                              Coordinate toCoordinate,
                              Connection connection) {
        Validate.notNull(connection);
        Validate.notNull(toCoordinate);
        this.player = player;
        this.realmId = realmId;
        this.toCoordinate = toCoordinate;
        this.connection = connection;
    }

    public int toRealm() {
        return realmId;
    }

    @Override
    public void accept(RealmEventHandler handler) {
        handler.teleportIn(player, toCoordinate, connection);
    }

    public static RealmTeleportEvent toDestination(Player player, int toRealm, Coordinate toCoordinate, Connection connection) {
        return new RealmTeleportEvent(player, toRealm, toCoordinate, connection);
    }
}
