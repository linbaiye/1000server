package org.y1000.kungfu;

import org.y1000.message.ValueEnum;

public enum KungFuType implements ValueEnum {

    QUANFA(0),

    SWORD(1),

    BLADE(2),

    SPEAR(4),

    AXE(3),

    BOW(5),

    THROW(6),

    FOOT(7),

    BREATHING(8),

    PROTECTION(9),

    ASSISTANT(10),

    NPC_SPELL(12),

    ;
    private final int v;

    KungFuType(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }


    public static KungFuType fromValue(int v) {
        return ValueEnum.fromValueOrThrow(values(), v);
    }
}
