package org.y1000.entities.objects;

import org.y1000.message.ValueEnum;

public enum DynamicObjectType implements ValueEnum  {
    TRIGGER(2),
    // Does nothing but occupies the ground.
    IMMUNE(0),

    //
    KILLABLE(1),

    YAOHUA(7),
    ;

    private final int v;

    DynamicObjectType(int v) {
        this.v = v;
    }


    @Override
    public int value() {
        return v;
    }

    public static DynamicObjectType fromValue(int v) {
        return ValueEnum.fromValueOrThrow(values(), v);
    }
}
