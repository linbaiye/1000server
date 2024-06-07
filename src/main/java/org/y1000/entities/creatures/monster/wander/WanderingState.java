package org.y1000.entities.creatures.monster.wander;

import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.entities.creatures.monster.MonsterState;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.entities.creatures.monster.fight.MonsterFightIdleState;

public interface WanderingState extends MonsterState<AbstractMonster> {

    @Override
    default void afterHurt(AbstractMonster creature) {
        creature.changeState(MonsterFightIdleState.start(creature));
    }
}
