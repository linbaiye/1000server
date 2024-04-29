package org.y1000.message.clientevent;

import org.y1000.entities.Direction;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.network.gen.ClientAttackEventPacket;

public record ClientAttackEvent(long sequence, long entityId, boolean below50, Direction direction) implements ClientEvent {

    public static ClientAttackEvent fromPacket(ClientAttackEventPacket packet) {
        return new ClientAttackEvent(packet.getSequence(), packet.getTargetId(), packet.getBelow50(), Direction.fromValue(packet.getDirection()));
    }

    @Override
    public void accept(PlayerImpl player, ClientEventVisitor handler) {
        handler.visit(player, this);
    }
}
