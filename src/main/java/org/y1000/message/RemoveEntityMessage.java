package org.y1000.message;

import org.y1000.network.gen.Packet;
import org.y1000.network.gen.RemoveEntityPacket;


public final class RemoveEntityMessage implements I2ClientMessage {

    private final long id;

    private final Packet packet;

    public RemoveEntityMessage(long id) {
        packet = createPacket(id);
        this.id = id;
    }

    private static Packet createPacket(long id) {
        return Packet.newBuilder()
                .setRemoveEntity(
                        RemoveEntityPacket.newBuilder()
                                .setId(id)
                                .build()

                ).build();
    }

    @Override
    public String toString() {
        return "RemoveEntityMessage{" +
                "id=" + id +
                '}';
    }

    @Override
    public Packet toPacket() {
        return packet;
    }
}
