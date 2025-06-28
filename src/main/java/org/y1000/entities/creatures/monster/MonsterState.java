package org.y1000.entities.creatures.monster;

import org.y1000.entities.creatures.ICreatureState;

public interface MonsterState<C extends AbstractMonster> extends ICreatureState<C> {

    /**
     * What to do after hurt.
     * @param creature
     */
    default void afterHurt(C creature) {

    }
}
