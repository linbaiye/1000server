package org.y1000.item;

import org.y1000.kungfu.attack.AttackKungFuType;

public final class Weapon extends AbstractEquipment {

    private final ItemSdb itemSdb;

    public Weapon(String name, ItemSdb itemSdb) {
        super(name, itemSdb.getSoundDrop(name), itemSdb.getSoundEvent(name));
        this.itemSdb = itemSdb;
    }

    public AttackKungFuType kungFuType() {
        return itemSdb.getAttackKungFuType(name());
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.WEAPON;
    }
}
