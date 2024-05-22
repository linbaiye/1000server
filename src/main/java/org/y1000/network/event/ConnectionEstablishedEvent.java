package org.y1000.network.event;

import org.y1000.entities.players.Player;
import org.y1000.network.Connection;
import org.y1000.network.ConnectionEventType;
import org.y1000.realm.event.PlayerConnectedEvent;

public record ConnectionEstablishedEvent(Player player,
                                         Connection connection) implements ConnectionEvent,
        PlayerConnectedEvent {
    @Override
    public ConnectionEventType type() {
        return ConnectionEventType.ESTABLISHED;
    }
}
