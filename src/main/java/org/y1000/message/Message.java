package org.y1000.message;

import org.y1000.connection.gen.Packet;

public interface Message {
    long sourceId();

    MessageType type();

    long timestamp();

    Packet toPacket();
}
