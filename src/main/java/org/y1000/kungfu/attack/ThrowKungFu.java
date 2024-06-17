package org.y1000.kungfu.attack;

import lombok.Builder;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.State;

@Slf4j
public class ThrowKungFu extends AbstractRangedKungFu {

    @Builder
    public ThrowKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    @Override
    public State randomAttackState() {
        return State.THROW;
    }


    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.THROW;
    }


    @Override
    protected Logger logger() {
        return log;
    }
}
