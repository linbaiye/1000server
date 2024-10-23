package org.y1000.item;

import org.y1000.entities.players.Damage;

public final class UpgradableWeapon extends AbstractWeapon implements Upgradable {

    private final Upgradable upgradable;

    public UpgradableWeapon(String name, ItemSdb itemSdb, Upgradable upgradable) {
        super(name, itemSdb);
        this.upgradable = upgradable;
    }
    public UpgradableWeapon(String name, ItemSdb itemSdb) {
        this(name, itemSdb, new UpgradableImpl());
    }

    @Override
    public int level() {
        return upgradable.level();
    }

    @Override
    public void upgrade() {
        upgradable.upgrade();
    }

    @Override
    public float percentage() {
        return upgradable.percentage();
    }

    @Override
    public int avoidance() {
        return getOriginAvoid()  + (int)(getOriginAvoid() * upgradable.percentage());
    }

    @Override
    public Damage damage() {
        return getOriginDamage().add(getOriginDamage().multiply(upgradable.percentage()));
    }
}
