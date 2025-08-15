package org.y1000.entities.players.event;

import org.y1000.network.I2ClientMessage;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ShowApplyKungFuWindowPacket;

public record ShowKungFuWindowMessage() implements I2ClientMessage {

    public final static ShowKungFuWindowMessage INSTANCE = new ShowKungFuWindowMessage();

    @Override
    public Packet toPacket() {
        return Packet.newBuilder()
                .setShowApplyKungFu(ShowApplyKungFuWindowPacket.newBuilder().build())
                .build();
    }
}
