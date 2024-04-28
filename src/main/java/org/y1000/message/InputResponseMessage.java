package org.y1000.message;

import org.y1000.network.gen.InputResponsePacket;
import org.y1000.network.gen.Packet;
import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.message.serverevent.EntityEventHandler;
import org.y1000.message.serverevent.PlayerEventHandler;

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
    public Entity source() {
        return positionMessage().source();
    }

    @Override
    public long id() {
        return positionMessage.id();
    }

    @Override
    public void accept(EntityEventHandler visitor) {
        if (visitor instanceof PlayerEventHandler playerEventVisitor) {
            playerEventVisitor.handle(this);
        }
    }
}
