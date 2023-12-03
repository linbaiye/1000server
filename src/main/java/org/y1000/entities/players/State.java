package org.y1000.entities.players;

import org.y1000.message.ValueEnum;

public enum State implements ValueEnum<State> {
    IDLE(1),

    WALK(2),
    ;

    private final int v;

    State(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }
}
