package org.y1000.message;

import org.y1000.connection.gen.InputResponsePacket;
import org.y1000.connection.gen.Packet;

public record InputResponseMessage(long sequence, AbstractPositionMessage positionMessage) implements I2ClientMessage {

    @Override
    public Packet toPacket() {
        return Packet.newBuilder()
                .setResponsePacket(InputResponsePacket.newBuilder()
                        .setPositionPacket(positionMessage.toPacket().getPositionPacket())
                        .setSequence(sequence)
                ).build();
    }
}
