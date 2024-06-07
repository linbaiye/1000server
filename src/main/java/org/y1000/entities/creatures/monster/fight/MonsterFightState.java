package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.entities.creatures.monster.MonsterState;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.util.Action;

public interface MonsterFightState extends MonsterState<AbstractMonster> {

    default void attackIfAdjacentOrNextMove(AbstractMonster monster, Action action) {
        if (monster.getFightingEntity() == null ||
                monster.getFightingEntity().coordinate().directDistance(monster.coordinate()) <= 1) {
            monster.fight();
        } else {
            action.invoke();
        }
    }
}
