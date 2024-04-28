package org.y1000.message.clientevent;

import org.y1000.entities.players.PlayerImpl;
import org.y1000.network.gen.AttackEventPacket;

public record ClientAttackEvent(long sequence, long entityId, boolean below50) implements ClientEvent {

    public static ClientAttackEvent fromPacket(AttackEventPacket packet) {
        return new ClientAttackEvent(packet.getSequence(), packet.getTargetId(), packet.getBelow50());
    }

    @Override
    public void accept(PlayerImpl player, ClientEventHandler handler) {
        handler.handle(player, this);
    }
}
