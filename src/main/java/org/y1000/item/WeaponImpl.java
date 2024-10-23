package org.y1000.item;

import org.y1000.entities.players.Damage;

public final class WeaponImpl extends AbstractWeapon {


    public WeaponImpl(String name, ItemSdb itemSdb) {
        super(name, itemSdb);
    }

    @Override
    public int avoidance() {
        return getOriginAvoid();
    }

    @Override
    public Damage damage() {
        return getOriginDamage();
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
