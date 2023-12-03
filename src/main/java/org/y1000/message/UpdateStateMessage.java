package org.y1000.message;

import org.y1000.connection.gen.Packet;

public interface UpdateStateMessage extends Message, I2ClientMessage {

    long sourceId();
}
