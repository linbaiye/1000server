package org.y1000.entities.projectile;

import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.monster.AbstractMonster;

public final class MonsterProjectile extends AbstractProjectile {

    public MonsterProjectile(AbstractMonster shooter,
                             PhysicalEntity target, int id) {
        super(shooter, target, id);
    }

    @Override
    public Damage damage() {
        return shooter().damage();
    }

    @Override
    public int hit() {
        return shooter().hit();
    }
}
