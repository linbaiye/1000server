package org.y1000.kungfu.attack;

import lombok.Builder;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.State;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class BladeKungFu extends AbstractMeleeKungFu {

    @Builder
    public BladeKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    @Override
    public State randomAttackState() {
       // return level() < 50 || ThreadLocalRandom.current().nextInt() % 2 == 1 ? State.BLADE : State.BLADE2H;
        return level() < 5000 || ThreadLocalRandom.current().nextInt() % 2 == 1 ? State.BLADE : State.BLADE2H;
    }

    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.BLADE;
    }


    @Override
    protected Logger logger() {
        return log;
    }
}
