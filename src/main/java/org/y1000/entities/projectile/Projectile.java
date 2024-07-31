package org.y1000.entities.projectile;

import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.players.Damage;
import org.y1000.entities.creatures.ViolentCreature;

public interface Projectile {

    ViolentCreature shooter();

    AttackableActiveEntity target();

    int flyingMillis();

    int projectileSpriteId();

    Damage damage();

    int hit();

    boolean update(int delta);

    Direction direction();
}
