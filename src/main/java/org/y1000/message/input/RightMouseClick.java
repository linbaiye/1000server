package org.y1000.message.input;

import org.y1000.connection.gen.InputPacket;
import org.y1000.entities.Direction;

public record RightMouseClick(long sequence, Direction direction) implements InputMessage {
    @Override
    public InputType type() {
        return InputType.MOUSE_RIGHT_CLICK;
    }

    public static RightMouseClick fromPacket(InputPacket inputPacket) {
        return new RightMouseClick(inputPacket.getSequence(), Direction.fromValue(inputPacket.getClickedDirection()));
    }

    @Override
    public String toString() {
        return "RightMouseClick{" +
                "sequence=" + sequence +
                ", direction=" + direction +
                '}';
    }
}
