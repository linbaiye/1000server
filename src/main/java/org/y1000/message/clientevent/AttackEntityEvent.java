package org.y1000.message.clientevent;

import org.y1000.network.gen.AttackEventPacket;

public record AttackEntityEvent(long sequence, long entityId, boolean below50)  implements ClientEvent {

    public static AttackEntityEvent fromPacket(AttackEventPacket packet) {
        return new AttackEntityEvent(packet.getSequence(), packet.getTargetId(), packet.getBelow50());
    }
}
