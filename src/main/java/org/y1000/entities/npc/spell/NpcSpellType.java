package org.y1000.entities.npc.spell;

import org.y1000.util.ValueEnum;

import java.util.Arrays;

public enum NpcSpellType implements ValueEnum {

    HIDE(0),

    Copy(1),

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
        return ValueEnum.getTypeOrThrow(values(), v);
    }

    public static boolean contains(int v) {
        return Arrays.stream(values()).map(NpcSpellType::value).anyMatch(val -> val == v);
    }

}
