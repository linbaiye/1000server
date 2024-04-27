package org.y1000.entities.players.kungfu.attack;

import org.y1000.message.ValueEnum;

public enum AttackKungFuType implements ValueEnum {

    QUANFA(0),

    SWORD(1),

    BLADE(2),

    SPEAR(3),

    AXE(4),
    ;

    private final int v;

    AttackKungFuType(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }
}
