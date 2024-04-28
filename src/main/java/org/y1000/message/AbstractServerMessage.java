package org.y1000.message;

import org.y1000.network.gen.Packet;

public abstract class AbstractServerMessage implements ServerMessage {

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
