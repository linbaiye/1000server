package org.y1000.network.event;

import org.y1000.network.Connection;
import org.y1000.network.ConnectionEventType;

public interface ConnectionEvent {

    Connection connection();

    ConnectionEventType type();

}
