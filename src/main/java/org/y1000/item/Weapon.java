package org.y1000.item;

import org.y1000.entities.players.Damage;
import org.y1000.kungfu.attack.AttackKungFuType;

public final class Weapon extends AbstractEquipment {

    private final ItemSdb itemSdb;

    private final Damage damage;

    public Weapon(String name, ItemSdb itemSdb) {
        super(name, itemSdb.getSoundDrop(name), itemSdb.getSoundEvent(name), itemSdb.getDesc(name));
        this.itemSdb = itemSdb;
        this.damage = new Damage(itemSdb.getDamageBody(name()), itemSdb.getDamageHead(name()), itemSdb.getDamageArm(name()), itemSdb.getDamageLeg(name()));
    }

    public AttackKungFuType kungFuType() {
        return itemSdb.getAttackKungFuType(name());
    }

    public int attackSpeed() {
        return itemSdb.getAttackSpeed(name());
    }

    public int avoidance() {
        return itemSdb.getAvoid(name());
    }

    public int recovery() {
        return itemSdb.getRecovery(name());
    }


    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.WEAPON;
    }

    public Damage damage() {
        return damage;
    }

}
