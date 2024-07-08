package org.y1000.item;

import org.y1000.entities.attribute.Damage;
import org.y1000.kungfu.attack.AttackKungFuType;

public final class Weapon extends AbstractEquipment {

    private final ItemSdb itemSdb;

    private final Damage damage;

    public Weapon(String name, ItemSdb itemSdb) {
        super(name, itemSdb.getSoundDrop(name), itemSdb.getSoundEvent(name));
        this.itemSdb = itemSdb;
        this.damage = new Damage(itemSdb.getDamageBody(name()), itemSdb.getDamageHead(name()), itemSdb.getDamageArm(name()), itemSdb.getDamageLeg(name()));
    }

    public AttackKungFuType kungFuType() {
        return itemSdb.getAttackKungFuType(name());
    }

    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.WEAPON;
    }

    public Damage damage() {
        return damage;
    }

}
