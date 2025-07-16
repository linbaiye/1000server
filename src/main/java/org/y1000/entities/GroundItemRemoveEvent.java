package org.y1000.entities;

import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.RemoveEntityPacket;

public class GroundItemRemoveEvent implements I2ClientMessage  {

    private final Packet packet;

    private GroundItemRemoveEvent(Packet packet) {
        this.packet = packet;
    }

    public static GroundItemRemoveEvent of(GroundItem item) {
        return new GroundItemRemoveEvent(Packet.newBuilder().setRemoveEntity(RemoveEntityPacket.newBuilder().setId(item.id())).build());
    }

    @Override
    public Packet toPacket() {
        return packet;
    }

}
