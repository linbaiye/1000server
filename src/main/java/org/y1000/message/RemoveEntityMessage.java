package org.y1000.message;

import org.y1000.network.gen.Packet;
import org.y1000.network.gen.RemoveEntityPacket;


public class RemoveEntityMessage extends AbstractServerMessage{

    private final long id;

    public RemoveEntityMessage(long id) {
        this.id = id;
    }

    public static Packet createPacket(long id) {
        return Packet.newBuilder()
                .setRemoveEntity(
                        RemoveEntityPacket.newBuilder()
                                .setId(id)
                                .build()

                ).build();
    }

    @Override
    protected Packet buildPacket() {
        return createPacket(id);
    }
}
