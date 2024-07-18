package org.y1000.item;

import org.y1000.entities.players.Damage;
import org.y1000.kungfu.attack.AttackKungFuType;

public interface Weapon extends Equipment {
    AttackKungFuType kungFuType();

    int attackSpeed();

    int avoidance();

    int recovery();

    Damage damage();
}
