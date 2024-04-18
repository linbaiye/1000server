package org.y1000.entities.creatures;

import org.y1000.message.ValueEnum;

public enum CreatureType implements ValueEnum {
    MONSTER(0),
    NPC(1),
    ;

    private final int v;

    CreatureType(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }
}
