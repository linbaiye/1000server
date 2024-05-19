package org.y1000.entities.creatures.monster;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.CreatureState;

public interface MonsterState<C extends Creature> extends CreatureState<C> {

    default void afterHurt(C c, Creature attacker) {
    }
}
