package org.y1000.network;

public record ConnectionEvent(ConnectionEventType type, Connection connection, Object data) {


    public static ConnectionEvent Close(Connection connection) {
        return new ConnectionEvent(ConnectionEventType.CLOSED, connection, null);
    }

    public static ConnectionEvent Data(Connection connection, Object data) {
        return new ConnectionEvent(ConnectionEventType.DATA, connection, data);
    }

}
