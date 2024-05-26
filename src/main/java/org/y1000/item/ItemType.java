package org.y1000.item;

import org.y1000.message.ValueEnum;

public enum ItemType implements ValueEnum  {
    EQUIPMENT(6),

    ARROW(7),

            ;

    private final int v;

    ItemType(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }

    public static ItemType fromValue(int v) {
        return ValueEnum.fromValueOrThrow(values(), v);
    }
}
