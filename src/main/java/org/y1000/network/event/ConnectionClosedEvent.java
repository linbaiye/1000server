package org.y1000.network.event;

import org.y1000.network.Connection;
import org.y1000.network.ConnectionEventType;

public record ConnectionClosedEvent(Connection connection) implements ConnectionEvent {
    @Override
    public ConnectionEventType type() {
        return ConnectionEventType.CLOSED;
    }
}
