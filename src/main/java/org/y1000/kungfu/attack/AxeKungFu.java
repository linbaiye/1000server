package org.y1000.kungfu.attack;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.State;

@Slf4j
public final class AxeKungFu extends AbstractMeleeKungFu {

    @Builder
    public AxeKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    @Override
    public State randomAttackState() {
        return State.AXE;
    }


    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.AXE;
    }



    @Override
    protected Logger logger() {
        return log;
    }
}
