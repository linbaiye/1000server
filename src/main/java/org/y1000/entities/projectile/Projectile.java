package org.y1000.entities.projectile;

import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.ViolentCreature;

public interface Projectile {

    ViolentCreature shooter();

    PhysicalEntity target();

    int flyingMillis();

    int projectileSpriteId();

    Damage damage();

    int hit();

    boolean update(int delta);
}
