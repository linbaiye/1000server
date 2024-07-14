package org.y1000.item;

import org.y1000.entities.players.Armor;
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


    @Override
    public String description() {
        StringBuilder descriptionBuilder = getDescriptionBuilder();
        descriptionBuilder.append("攻击速度: ").append(attackSpeed()).append("\n");
        descriptionBuilder.append("恢复: ").append(recovery()).append("\t")
                .append("闪躲: ").append(avoidance()).append("\n");
        Damage dmg = damage();
        descriptionBuilder.append(String.format("破坏力: %d / %d / %d / %d", dmg.bodyDamage(), dmg.headDamage(), dmg.armDamage(), dmg.legDamage()));
        return descriptionBuilder.toString();
    }
}
