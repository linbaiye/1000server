package org.y1000.message;

import org.y1000.connection.gen.Packet;

public interface I2ClientMessage {

    Packet toPacket();

}
