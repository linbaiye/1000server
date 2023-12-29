package org.y1000.message.input;

import org.y1000.entities.Direction;

public record RightMousePressedMotion(long sequence, long timestamp, Direction direction) implements InputMessage {
    @Override
    public InputType type() {
        return InputType.MOUSE_RIGHT_MOTION;
    }
}
