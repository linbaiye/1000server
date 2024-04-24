package org.y1000.network;

import org.y1000.message.clientevent.ClientEvent;

public interface NetworkEventListener {

    default void OnEvent(ConnectionEventType type, Connection connection) { }


    default void OnClosed(Connection connection) {

    }

    default void OnEstablished(Connection connection) {

    }
}
