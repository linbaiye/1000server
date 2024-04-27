package org.y1000.entities.players.equipment.weapon;

import org.y1000.message.ValueEnum;

public enum WeaponType implements ValueEnum {
    FIST(0),
    SWORD(1),
    ;


    private final int v;

    WeaponType(int v) {
        this.v = v;
    }

    @Override
    public int value() {
        return v;
    }
}
