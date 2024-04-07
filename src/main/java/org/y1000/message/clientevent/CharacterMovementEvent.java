package org.y1000.message.clientevent;

import org.y1000.connection.gen.ClientPacket;
import org.y1000.connection.gen.InputPacket;
import org.y1000.connection.gen.MoveEventPacket;
import org.y1000.message.ValueEnum;
import org.y1000.message.input.*;
import org.y1000.util.Coordinate;

public record CharacterMovementEvent(InputMessage inputMessage, Coordinate happenedAt) implements ClientEvent {

    @Override
    public String toString() {
        return "CharacterMovementEvent{" +
                "inputMessage=" + inputMessage +
                ", happenedAt=" + happenedAt +
                '}';
    }

    public static CharacterMovementEvent fromPacket(ClientPacket clientPacket) {
        MoveEventPacket moveEventPacket = clientPacket.getMoveEventPacket();
        InputType type = ValueEnum.fromValueOrThrow(InputType.values(),
                moveEventPacket.getInput().getType());
        var coor = new Coordinate(moveEventPacket.getHappenedAtX(), moveEventPacket.getHappenedAtY());
        return switch (type) {
            case MOUSE_RIGHT_CLICK -> new CharacterMovementEvent(RightMouseClick.fromPacket(moveEventPacket.getInput()), coor);
            case MOUSE_RIGHT_RELEASE -> new CharacterMovementEvent(RightMouseRelease.fromPacket(moveEventPacket.getInput()), coor);
            case MOUSE_RIGHT_MOTION -> new CharacterMovementEvent(RightMousePressedMotion.fromPacket(moveEventPacket.getInput()), coor);
            default -> throw new IllegalArgumentException();
        };
    }
}
