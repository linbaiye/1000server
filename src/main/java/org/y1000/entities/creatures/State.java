package org.y1000.entities.creatures;

import org.y1000.message.ValueEnum;

public enum State implements ValueEnum {
    IDLE(1),

    WALK(2),

    RUN(3),

    STANDUP(4),

    HURT(6),

    DIE(7),

    ENFIGHT_WALK(8),

    BOW(9),

    SIT(10),

    FLY(11),

    ATTACK(12),

    COOLDOWN(13),

    HELLO(14),

    FIST(15),

    KICK(16),

    SWORD(17),

    SWORD2H(18),

    BLADE(19),

    BLADE2H(20),

    AXE(21),

    SPEAR(22),

    THROW(23),

    FROZEN(24),

    ;

    private final int v;

    State(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }

    public static State valueOf(int v) {
        return ValueEnum.fromValueOrThrow(State.values(), v);
    }
}
