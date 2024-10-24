package org.y1000.item;

import org.y1000.entities.players.Damage;
import org.y1000.kungfu.attack.AttackKungFuType;

import java.util.HashSet;
import java.util.Set;

public final class WeaponImpl extends AbstractEquipment implements Weapon {

    private final ItemSdb itemSdb;

    private final Damage damage;

    public WeaponImpl(String name, ItemSdb itemSdb) {
        this(name, itemSdb, new HashSet<>());
    }

    public WeaponImpl(String name, ItemSdb itemSdb, Set<Object> abilities) {
        super(name, itemSdb, abilities);
        this.itemSdb = itemSdb;
        this.damage = new Damage(itemSdb.getDamageBody(name()), itemSdb.getDamageHead(name()), itemSdb.getDamageArm(name()), itemSdb.getDamageLeg(name()));
    }

    @Override
    public AttackKungFuType kungFuType() {
        return itemSdb.getAttackKungFuType(name());
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

    private int getOriginAvoid() {
        return itemSdb.getAvoid(name());
    }

    @Override
    public int avoidance() {
        return findAbility(Upgradable.class)
                .map(upgradable -> getOriginAvoid() + (int)(getOriginAvoid() * upgradable.percentage()))
                .orElseGet(this::getOriginAvoid);
    }

    @Override
    public Damage damage() {
        return findAbility(Upgradable.class)
                .map(upgradable -> damage.add(damage.multiply(upgradable.percentage())))
                .orElse(damage);
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
