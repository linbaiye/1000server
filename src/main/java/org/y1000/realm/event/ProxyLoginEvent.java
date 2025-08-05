package org.y1000.realm.event;

import org.y1000.network.Connection;
import org.y1000.util.Coordinate;

public final class ProxyLoginEvent implements IdentifiedRealmEvent {

    private final int reamlId;

    private final long playerId;

    private final Coordinate coordinate;

    private final Connection connection;

    public ProxyLoginEvent(int reamlId, long playerId, Coordinate coordinate, Connection connection) {
        this.reamlId = reamlId;
        this.playerId = playerId;
        this.coordinate = coordinate;
        this.connection = connection;
    }


    @Override
    public int toRealm() {
        return reamlId;
    }

    @Override
    public void accept(RealmEventHandler handler) {
        handler.handleProxiedLogin(playerId, coordinate, connection);
    }
}
