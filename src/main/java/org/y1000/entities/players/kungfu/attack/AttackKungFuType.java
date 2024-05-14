package org.y1000.entities.players.kungfu.attack;

import org.y1000.message.ValueEnum;

public enum AttackKungFuType implements ValueEnum {

    QUANFA(0, 400, 560),

    SWORD(1, 0, 0),

    BLADE(2, 0, 0),

    SPEAR(3, 0, 0),

    AXE(4, 0, 0),
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
}
