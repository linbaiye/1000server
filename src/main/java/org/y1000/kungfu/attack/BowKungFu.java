package org.y1000.kungfu.attack;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.State;

@Slf4j
@SuperBuilder
public final class BowKungFu extends AbstractRangedKungFu {

    @Override
    public State randomAttackState() {
        return State.BOW;
    }


    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.BOW;
    }


    @Override
    protected Logger logger() {
        return log;
    }
}
