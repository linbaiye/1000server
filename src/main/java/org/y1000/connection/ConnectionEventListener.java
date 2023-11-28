package org.y1000.connection;

public interface ConnectionEventListener {

    void OnEvent(ConnectionEventType type, Connection connection);

}
