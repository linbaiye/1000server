package org.y1000.entities.creatures;

import org.y1000.message.ValueEnum;

public enum NpcType implements ValueEnum  {
    MONSTER(1),
    MERCHANT(2),
    GUARDIAN(3),
    BANKER(4),
    QUESTER(5),
    MERCHANT_QUESTER(6),
    INTERACTABLE(7),
    ;
    private final int v;


    NpcType(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }
}
