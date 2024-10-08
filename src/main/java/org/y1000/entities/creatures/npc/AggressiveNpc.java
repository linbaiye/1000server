package org.y1000.entities.creatures.npc;

import org.y1000.entities.AttackableActiveEntity;

public interface AggressiveNpc extends ViolentNpc {


    void actAggressively(AttackableActiveEntity enemy);


    default boolean canActAggressively(AttackableActiveEntity enemy) {
        return canChaseOrAttack(enemy) &&
                coordinate().directDistance(enemy.coordinate()) <= viewWidth();
    }

}
