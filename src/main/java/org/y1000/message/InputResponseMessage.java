package org.y1000.message;

import org.y1000.network.gen.InputResponsePacket;
import org.y1000.network.gen.Packet;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.message.serverevent.PlayerEventVisitor;

public record InputResponseMessage(long sequence, AbstractPositionEvent positionMessage) implements EntityEvent,
        ServerMessage {

    @Override
    public Packet toPacket() {
        return Packet.newBuilder()
                .setResponsePacket(InputResponsePacket.newBuilder()
                        .setPositionPacket(positionMessage.toPacket().getPositionPacket())
                        .setSequence(sequence)
                ).build();
    }

    public Player player() {
        return (Player) source();
    }

    @Override
    public PhysicalEntity source() {
        return positionMessage().source();
    }


    @Override
    public void accept(EntityEventVisitor visitor) {
        if (visitor instanceof PlayerEventVisitor playerEventVisitor) {
            playerEventVisitor.visit(this);
        }
    }
}
