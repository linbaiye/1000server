package org.y1000.entities.players.kungfu.attack;

import org.y1000.message.ValueEnum;

public enum AttackKungFuType implements ValueEnum {

    QUANFA(0, 400, 560),

    SWORD(1, 720, 1100),

    BLADE(2, 720, 630),

    SPEAR(4, 800, 800),

    AXE(3, 800, 800),

    BOW(5, 600, 600),

    THROW(6, 600, 600),
    ;

    private final int v;

    private final int below50;

    private final int above50;


    AttackKungFuType(int v, int below50, int above50) {
        this.v = v;
        this.below50 = below50;
        this.above50 = above50;
    }

    @Override
    public int value() {
        return v;
    }

    public int below50Millis() {
        return below50;
    }

    public int above50Millis() {
        return above50;
    }

    public static AttackKungFuType fromValue(int v) {
        return ValueEnum.fromValueOrThrow(values(), v);
    }
}
