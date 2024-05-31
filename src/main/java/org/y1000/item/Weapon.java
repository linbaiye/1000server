package org.y1000.item;

import lombok.Builder;
import org.y1000.kungfu.attack.AttackKungFuType;

public final class Weapon extends AbstractEquipment {

    private final AttackKungFuType attackKungFuType;

    @Builder
    public Weapon(String name, AttackKungFuType attackKungFuType) {
        super(name);
        this.attackKungFuType = attackKungFuType;
    }

    public AttackKungFuType kungFuType() {
        return attackKungFuType;
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.WEAPON;
    }
}
