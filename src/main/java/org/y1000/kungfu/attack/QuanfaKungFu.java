package org.y1000.kungfu.attack;

import lombok.Builder;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.State;
import org.y1000.kungfu.KungFu;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class QuanfaKungFu extends AbstractMeleeKungFu {

    @Builder
    public QuanfaKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    @Override
    public State randomAttackState() {
        return level() < 5000 || ThreadLocalRandom.current().nextInt(0, 2) == 1 ? State.FIST: State.KICK;
    }


    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.QUANFA;
    }



    @Override
    protected Logger logger() {
        return log;
    }

    @Override
    public KungFu duplicate() {
        return new QuanfaKungFu(name(), 0, getParameters());
    }
}
