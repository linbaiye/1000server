package org.y1000.entities.players;

import org.y1000.message.ValueEnum;

public enum State implements ValueEnum {
    IDLE(1),

    WALK(2),

    RUN(3),

    FLY(11),

    ATTACK(12),
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
