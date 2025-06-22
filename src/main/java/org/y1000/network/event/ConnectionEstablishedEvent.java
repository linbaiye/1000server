package org.y1000.network.event;

import org.y1000.entities.players.Player;
import org.y1000.network.Connection;
import org.y1000.network.ConnectionEventType;
import org.y1000.realm.event.PlayerRealmEvent;

public record ConnectionEstablishedEvent(int toRealmId, Player player,
                                         Connection connection)
        implements IConnectionEvent, PlayerRealmEvent {
    @Override
    public ConnectionEventType type() {
        return ConnectionEventType.CLOSED;
    }
}
