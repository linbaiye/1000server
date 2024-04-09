package org.y1000.message;

import org.y1000.connection.gen.InputResponsePacket;
import org.y1000.connection.gen.Packet;

import java.util.Optional;

public record InputResponseMessage(long sequence, AbstractPositionMessage positionMessage) implements ServerEvent {

    @Override
    public Packet toPacket() {
        return Packet.newBuilder()
                .setResponsePacket(InputResponsePacket.newBuilder()
                        .setPositionPacket(positionMessage.toPacket().getPositionPacket())
                        .setSequence(sequence)
                ).build();
    }

    @Override
    public Optional<ServerEvent> eventToPlayer(long id) {
        return id != this.positionMessage().id() ?
            Optional.of(positionMessage) : Optional.empty();
    }
}
