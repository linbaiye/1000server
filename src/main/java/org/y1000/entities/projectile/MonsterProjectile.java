package org.y1000.entities.projectile;

import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.monster.AbstractMonster;

public class MonsterProjectile extends AbstractProjectile<AbstractMonster> {

    public MonsterProjectile(AbstractMonster shooter,
                             PhysicalEntity target, int id) {
        super(shooter, target, id);
    }

}
