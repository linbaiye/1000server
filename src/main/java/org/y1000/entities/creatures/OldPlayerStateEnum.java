package org.y1000.entities.creatures;

import org.y1000.message.ValueEnum;

@Deprecated
public enum OldPlayerStateEnum implements ValueEnum {
    IDLE(1),

    Move(2),

    RUN(3),

    STANDUP(4),

    HURT(6),

    DIE(7),

    ENFIGHT_WALK(8),

    BOW(9),

    SIT(10),

    FLY(11),

    ATTACK(12),

    FightStand(13),

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

    Turn(24),

    ;

    private final int v;

    OldPlayerStateEnum(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }

    public static OldPlayerStateEnum valueOf(int v) {
        return ValueEnum.getTypeOrThrow(OldPlayerStateEnum.values(), v);
    }
}
