package org.y1000.message.clientevent.input;


import org.y1000.network.gen.InputPacket;
import org.y1000.entities.Direction;

public final class RightMouseClick extends AbstractRightClick {
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
