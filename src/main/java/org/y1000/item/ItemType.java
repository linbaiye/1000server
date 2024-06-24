package org.y1000.item;

import org.y1000.message.ValueEnum;

public enum ItemType implements ValueEnum  {
    EQUIPMENT(6),

    ARROW(7),

    SELLING_GOODS(5),

    STACK(8),

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
        if (v == 4) {
            v = 5;
        }
        return ValueEnum.fromValueOrThrow(values(), v);
    }
}
