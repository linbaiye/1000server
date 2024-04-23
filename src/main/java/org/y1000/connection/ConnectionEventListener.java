package org.y1000.connection;

import org.y1000.message.clientevent.ClientEvent;

public interface ConnectionEventListener {

    default void OnEvent(ConnectionEventType type, Connection connection) { }


    default void OnClosed(Connection connection) {

    }

    default void OnEstablished(Connection connection) {

    }

    default void OnEventArrived(Connection connection, ClientEvent clientEvent) {

    }

}
