package org.y1000.entities.creatures.monster;

import org.y1000.entities.creatures.CreatureState;

public interface MonsterState<C extends AbstractMonster> extends CreatureState<C> {

    /**
     * What to do after hurt.
     * @param creature
     */
    void afterHurt(C creature);
}
