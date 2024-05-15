package org.y1000.message.clientevent.input;

import org.y1000.network.gen.InputPacket;
import org.y1000.entities.Direction;

public final class RightMousePressedMotion extends AbstractRightClick {

    public RightMousePressedMotion(long sequence, Direction direction) {
        super(sequence, direction);
    }

    @Override
    public InputType type() {
        return InputType.MOUSE_RIGHT_MOTION;
    }

    public static RightMousePressedMotion fromPacket(InputPacket packet) {
        return new RightMousePressedMotion(packet.getSequence(), Direction.fromValue(packet.getClickedDirection()));
    }
}
