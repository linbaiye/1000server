package org.y1000.item;

import org.y1000.entities.players.Damage;
import org.y1000.kungfu.attack.AttackKungFuType;

public abstract class AbstractWeapon extends AbstractEquipment implements Weapon {

    private final ItemSdb itemSdb;

    private Long id;

    private final Damage damage;

    public AbstractWeapon(String name, ItemSdb itemSdb) {
        super(name, itemSdb.getSoundDrop(name), itemSdb.getSoundEvent(name), itemSdb.getDesc(name));
        this.itemSdb = itemSdb;
        this.damage = new Damage(itemSdb.getDamageBody(name()), itemSdb.getDamageHead(name()), itemSdb.getDamageArm(name()), itemSdb.getDamageLeg(name()));
    }

    @Override
    public AttackKungFuType kungFuType() {
        return itemSdb.getAttackKungFuType(name());
    }

    protected int getOriginAvoid() {
        return itemSdb.getAvoid(name());
    }

    protected Damage getOriginDamage() {
        return damage;
    }

    @Override
    public int attackSpeed() {
        return itemSdb.getAttackSpeed(name());
    }

    @Override
    public int recovery() {
        return itemSdb.getRecovery(name());
    }
    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.WEAPON;
    }
}
