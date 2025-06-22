package org.y1000.message;

import org.y1000.network.gen.Packet;


public interface I2ClientMessage {

    Packet toPacket();

}
