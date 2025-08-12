package org.y1000.item;

import org.y1000.util.ValueEnum;

public enum EquipmentType implements ValueEnum {
    WEAPON(9),
    HAT(8),
    CHEST(6),
    CLOTHING(2),
    BOOT(3),
    TROUSER(4),
    WRIST(1),
    HAIR(7),
    ;
    private final int v;

    EquipmentType(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }

    public static EquipmentType fromValue(int v) {
        return ValueEnum.getTypeOrThrow(values(), v);
    }
}
