package org.y1000.entities.creatures.npc.spell;

import org.y1000.message.ValueEnum;

import java.util.Arrays;

public enum NpcSpellType implements ValueEnum {

    HIDE(0),

    CLONE(1),

    HEAL(2),

    SHIFT(3),


    ;

    private final int v;

    NpcSpellType(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }

    public static NpcSpellType fromValue(int v) {
        return ValueEnum.fromValueOrThrow(values(), v);
    }

    public static boolean contains(int v) {
        return Arrays.stream(values()).map(NpcSpellType::value).anyMatch(val -> val == v);
    }

}
