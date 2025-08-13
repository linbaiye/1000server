package org.y1000.entities.players.equipment;

import org.y1000.entities.players.Damage;
import org.y1000.item.ItemSdb;
import org.y1000.item.Upgradable;
import org.y1000.kungfu.attack.AttackKungFuType;

import java.util.HashSet;
import java.util.Set;

public final class WeaponImpl extends AbstractEquipment implements Weapon {


    private final Damage damage;

    private final int attackSpeed;
    private final int recovery;
    private final int avoid;
    private final AttackKungFuType kungFuType;

    public WeaponImpl(String name, ItemSdb itemSdb) {
        this(name, itemSdb, new HashSet<>());
    }

    private WeaponImpl(String name, Damage damage,
                       int attackSpeed, int recovery, int avoid,
                       ItemSdb itemSdb,
                       Set<EquipmentAbility> abilities) {
        super(name, itemSdb, abilities);
        this.attackSpeed = attackSpeed;
        this.damage = damage;
        this.kungFuType = itemSdb.getAttackKungFuType(name);
        this.recovery = recovery;
        this.avoid = avoid;
    }

    public WeaponImpl(String name, ItemSdb itemSdb, Set<EquipmentAbility> abilities) {
        this(name, new Damage(itemSdb.getDamageBody(name), itemSdb.getDamageHead(name), itemSdb.getDamageArm(name), itemSdb.getDamageLeg(name)),
                itemSdb.getAttackSpeed(name),itemSdb.getRecovery(name), itemSdb.getAvoid(name), itemSdb, abilities );
    }

    public static WeaponImpl randomAttribute(String name, ItemSdb itemSdb, Set<Object> abilities) {
        return null;
    }

    @Override
    public AttackKungFuType kungFuType() {
        return kungFuType;
    }

    @Override
    public int attackSpeed() {
        return attackSpeed;
    }

    @Override
    public int recovery() {
        return recovery;
    }
    @Override
    public EquipmentType equipmentType() {
        return EquipmentType.WEAPON;
    }

    private int getOriginAvoid() {
        return avoid;
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
        descriptionBuilder.append("恢复: ").append(recovery()).append("\n")
                .append("闪躲: ").append(avoidance()).append("\n");
        Damage dmg = damage();
        descriptionBuilder.append(String.format("破坏力: %d / %d / %d / %d", dmg.bodyDamage(), dmg.headDamage(), dmg.armDamage(), dmg.legDamage()));
        return descriptionBuilder.toString();
    }
}
