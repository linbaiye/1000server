package org.y1000.kungfu.attack;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.players.AttackAction;
import org.y1000.kungfu.KungFu;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class BladeKungFu extends AbstractMeleeKungFu {

    @Builder
    public BladeKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.BLADE;
    }

    @Override
    public AttackAction computeAttackAction() {
        return level() < 5000 || ThreadLocalRandom.current().nextInt(0, 2) == 1 ? AttackAction.Blade1H: AttackAction.Blade2H;
    }


    @Override
    protected Logger logger() {
        return log;
    }

    @Override
    public KungFu duplicate() {
        return new BladeKungFu(name(), exp(), getParameters());
    }
}
