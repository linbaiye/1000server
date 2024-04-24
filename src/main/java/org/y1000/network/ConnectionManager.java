package org.y1000.network;


public interface ConnectionManager {

    void OnEstablished(Connection connection);

    void OnClosed(Connection connection);
}
