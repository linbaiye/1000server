package org.y1000.item;

import lombok.Getter;

@Getter
public abstract class AbstractArmorEquipment extends AbstractEquipment {

    private final boolean male;

    public AbstractArmorEquipment(String name, boolean male) {
        super(name);
        this.male = male;
    }

    public int avoidance() {
        return 0;
    }

    public int headArmor() {
        return 0;
    }

    public int armor() {
        return 0;
    }

    public int armArmor() {
        return 0;
    }

    public int legArmor() {
        return 0;
    }

    public int recovery() {
        return 0;
    }
}
