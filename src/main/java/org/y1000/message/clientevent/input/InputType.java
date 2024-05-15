package org.y1000.message.clientevent.input;

import org.y1000.message.ValueEnum;

public enum InputType implements ValueEnum {
    MOUSE_RIGHT_CLICK(1),

    MOUSE_RIGHT_RELEASE(2),

    MOUSE_RIGHT_MOTION(3),

    ;
    private final int v;

    InputType(int v) {
        this.v = v;
    }

    public int value() {
        return v;
    }
}
