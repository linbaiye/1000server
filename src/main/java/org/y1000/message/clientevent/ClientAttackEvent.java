package org.y1000.message.clientevent;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.network.gen.ClientAttackEventPacket;

public record ClientAttackEvent(long sequence, long entityId, State attackState, Direction direction) implements ClientEvent {

    public static ClientAttackEvent fromPacket(ClientAttackEventPacket packet) {
        return new ClientAttackEvent(packet.getSequence(), packet.getTargetId(), State.valueOf(packet.getState()), Direction.fromValue(packet.getDirection()));
    }

    @Override
    public void accept(PlayerImpl player, BiClientEventVisitor handler) {
        handler.visit(player, this);
    }
}
