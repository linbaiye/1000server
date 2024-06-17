package org.y1000.kungfu.attack;

import lombok.Builder;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.State;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class QuanfaKungFu extends AbstractMeleeKungFu {

    @Builder
    public QuanfaKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    @Override
    public State randomAttackState() {
        //return level() < 50 || ThreadLocalRandom.current().nextInt() % 2 == 1 ? State.FIST: State.KICK;
        return level() < 5000 || ThreadLocalRandom.current().nextInt() % 2 == 1 ? State.FIST: State.KICK;
    }


    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.QUANFA;
    }



    @Override
    protected Logger logger() {
        return log;
    }
}
