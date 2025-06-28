package org.y1000.message.input;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.PlayerStateEnum;
import org.y1000.network.gen.ClientAttackEventPacket;

public record ClientAttackEvent(long sequence, long entityId, PlayerStateEnum attackPlayerStateEnum, Direction direction) implements ClientEvent {

    public static ClientAttackEvent fromPacket(ClientAttackEventPacket packet) {
        return new ClientAttackEvent(packet.getSequence(), packet.getTargetId(), PlayerStateEnum.valueOf(packet.getState()), Direction.fromValue(packet.getDirection()));
    }

}
