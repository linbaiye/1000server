package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.monster.MonsterState;
import org.y1000.entities.creatures.monster.PassiveMonster;

interface MonsterFightingState extends MonsterState<PassiveMonster> {

    default Creature currentTarget() {
        return null;
    }

    @Override
    default void afterHurt(PassiveMonster monster, Creature attacker) {
        if (attacker.coordinate().directDistance(monster.coordinate()) <
                currentTarget().coordinate().directDistance(monster.coordinate())) {
            monster.attack(attacker);
        } else {
            monster.attack(currentTarget());
        }
    }
}
