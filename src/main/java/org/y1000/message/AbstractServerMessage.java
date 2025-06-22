package org.y1000.message;

import org.y1000.network.gen.Packet;

public abstract class AbstractServerMessage implements I2ClientMessage {

    private Packet packet;

    protected abstract Packet buildPacket();

    @Override
    public Packet toPacket() {
        if (packet == null) {
            packet = buildPacket();
        }
        return packet;
    }
}
