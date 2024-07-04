package org.y1000.item;

import org.y1000.message.ValueEnum;

public enum ItemType implements ValueEnum  {
    MONEY(3),

    SELLING_GOODS(5),

    EQUIPMENT(6),

    ARROW(7),

    KNIFE(8),

    PILL(13),

    STACK(Integer.MAX_VALUE),

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
