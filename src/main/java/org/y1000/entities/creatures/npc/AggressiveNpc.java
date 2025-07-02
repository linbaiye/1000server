package org.y1000.entities.creatures.npc;

import org.y1000.entities.AttackableEntity;

public interface AggressiveNpc extends ViolentNpc {


    void actAggressively(AttackableEntity enemy);


    default boolean canActAggressively(AttackableEntity enemy) {
        return canChaseOrAttack(enemy) &&
                coordinate().directDistance(enemy.coordinate()) <= viewWidth();
    }

}
