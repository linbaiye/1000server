package org.y1000.message.input;

import org.y1000.message.ValueEnum;

public enum InputType implements ValueEnum<InputType> {
    MOUSE_RIGHT_CLICK(1),

    ;
    private final int v;

    InputType(int v) {
        this.v = v;
    }

    public int value() {
        return v;
    }
}
