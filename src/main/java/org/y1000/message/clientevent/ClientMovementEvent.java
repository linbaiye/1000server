package org.y1000.message.clientevent;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.message.clientevent.input.*;
import org.y1000.network.gen.ClientPacket;
import org.y1000.network.gen.MoveEventPacket;
import org.y1000.message.ValueEnum;
import org.y1000.util.Coordinate;

@Slf4j
public record ClientMovementEvent(InputMessage moveInput, Coordinate happenedAt) implements ClientEvent {

    @Override
    public String toString() {
        return "CharacterMovementEvent{" +
                "moveInput=" + moveInput +
                ", happenedAt=" + happenedAt +
                '}';
    }

    public static ClientMovementEvent fromPacket(ClientPacket clientPacket) {
        MoveEventPacket moveEventPacket = clientPacket.getMoveEventPacket();
        InputType type = ValueEnum.fromValueOrThrow(InputType.values(),
                moveEventPacket.getInput().getType());
        var coor = new Coordinate(moveEventPacket.getHappenedAtX(), moveEventPacket.getHappenedAtY());
        return switch (type) {
            case MOUSE_RIGHT_CLICK -> new ClientMovementEvent(RightMouseClick.fromPacket(moveEventPacket.getInput()), coor);
            case MOUSE_RIGHT_RELEASE -> new ClientMovementEvent(RightMouseRelease.fromPacket(moveEventPacket.getInput()), coor);
            case MOUSE_RIGHT_MOTION -> new ClientMovementEvent(RightMousePressedMotion.fromPacket(moveEventPacket.getInput()), coor);
        };
    }

    @Override
    public void accept(PlayerImpl player, ClientEventVisitor handler) {
        handler.visit(player, this);
    }
}
