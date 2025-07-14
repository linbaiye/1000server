package org.y1000.entities.projectile;

import org.y1000.entities.Direction;
import org.y1000.entities.players.Damage;
import org.y1000.entities.ActiveEntity;

public interface Projectile {

    ActiveEntity shooter();

    ActiveEntity target();

    int flyingMillis();

    String sprite();

    Damage damage();

    int hit();

    boolean update(int delta);

    Direction direction();
}
