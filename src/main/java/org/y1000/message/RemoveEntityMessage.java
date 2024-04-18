package org.y1000.message;

import org.y1000.connection.gen.Packet;


public class RemoveEntityMessage implements ServerMessage {
    private final long id;

    public RemoveEntityMessage(long id) {
        this.id = id;
    }

    @Override
    public Packet toPacket() {
        return Packet.newBuilder()
                .build();
    }
}
