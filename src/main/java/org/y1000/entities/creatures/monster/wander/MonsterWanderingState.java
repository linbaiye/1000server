package org.y1000.entities.creatures.monster.wander;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.monster.MonsterState;
import org.y1000.entities.creatures.monster.PassiveMonster;

interface MonsterWanderingState extends MonsterState<PassiveMonster> {

    @Override
    default void afterHurt(PassiveMonster passiveMonster, Creature attacker) {
        passiveMonster.attack(attacker);
    }
}
