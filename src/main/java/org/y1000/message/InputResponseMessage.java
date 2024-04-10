package org.y1000.message;

import org.y1000.connection.gen.InputResponsePacket;
import org.y1000.connection.gen.Packet;
import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;

import java.util.Optional;

public record InputResponseMessage(long sequence, AbstractPositionEvent positionMessage) implements EntityEvent {

    @Override
    public Packet toPacket() {
        return Packet.newBuilder()
                .setResponsePacket(InputResponsePacket.newBuilder()
                        .setPositionPacket(positionMessage.toPacket().getPositionPacket())
                        .setSequence(sequence)
                ).build();
    }

    @Override
    public void accept(ServerEventVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Optional<ServerEvent> eventToPlayer(long id) {
        return id != this.positionMessage().id() ?
            Optional.of(positionMessage) : Optional.empty();
    }

    public Player player() {
        return (Player) source();
    }

    @Override
    public Entity source() {
        return positionMessage().source();
    }

    @Override
    public long id() {
        return positionMessage.id();
    }
}
