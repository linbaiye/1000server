package org.y1000.message;

import org.y1000.connection.gen.Packet;

public interface ServerEvent {

    Packet toPacket();

}
