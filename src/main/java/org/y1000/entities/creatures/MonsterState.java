package org.y1000.entities.creatures;

public interface MonsterState<C extends Creature> extends CreatureState<C> {

    default void afterAttacked(C c, Creature attacker) {
    }
}
