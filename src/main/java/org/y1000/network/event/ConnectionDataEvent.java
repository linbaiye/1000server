package org.y1000.network.event;

import org.y1000.message.clientevent.ClientEvent;
import org.y1000.network.Connection;
import org.y1000.network.ConnectionEventType;

public record ConnectionDataEvent(Connection connection,
                                  ClientEvent data) implements ConnectionEvent {
    @Override
    public ConnectionEventType type() {
        return ConnectionEventType.DATA;
    }
}
