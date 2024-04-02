package org.y1000.message.input;

import org.y1000.connection.gen.InputPacket;
import org.y1000.entities.Direction;

public class RightMouseClick extends AbstractRightClick {
    public RightMouseClick(long sequence, Direction direction) {
        super(sequence, direction);
    }
    @Override
    public InputType type() {
        return InputType.MOUSE_RIGHT_CLICK;
    }

    public static RightMouseClick fromPacket(InputPacket inputPacket) {
        return new RightMouseClick(inputPacket.getSequence(), Direction.fromValue(inputPacket.getClickedDirection()));
    }
}
