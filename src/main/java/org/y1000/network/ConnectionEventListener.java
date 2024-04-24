package org.y1000.network;

public interface ConnectionEventListener {

    void OnEstablished(Connection connection);

    void OnClosed(Connection connection);
}
