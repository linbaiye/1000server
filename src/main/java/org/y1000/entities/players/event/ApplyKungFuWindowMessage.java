package org.y1000.entities.players.event;

import org.y1000.network.I2ClientMessage;
import org.y1000.network.gen.ApplyKungFuWindowPacket;
import org.y1000.network.gen.Packet;

public record ApplyKungFuWindowMessage(Packet packet) implements I2ClientMessage {

    public static ApplyKungFuWindowMessage close() {
        return new ApplyKungFuWindowMessage(Packet.newBuilder()
                .setShowApplyKungFu(ApplyKungFuWindowPacket.newBuilder()
                        .setType(2)
                        .build())
                .build());
    }

    public static ApplyKungFuWindowMessage message(String msg) {
        return new ApplyKungFuWindowMessage(Packet.newBuilder()
                .setShowApplyKungFu(ApplyKungFuWindowPacket.newBuilder()
                        .setType(3)
                        .setMsg(msg)
                        .build()).build());
    }

    public static ApplyKungFuWindowMessage open() {
        return new ApplyKungFuWindowMessage(Packet.newBuilder()
                .setShowApplyKungFu(ApplyKungFuWindowPacket.newBuilder()
                        .setType(1)
                        .build())
                .build());
    }

    @Override
    public Packet toPacket() {
        return packet;
    }
}
