package org.y1000.kungfu.attack;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.players.AttackAction;
import org.y1000.kungfu.KungFu;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class SwordKungFu extends AbstractMeleeKungFu {

    @Builder
    public SwordKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.SWORD;
    }

    @Override
    public AttackAction computeAttackAction() {
        return level() < 5000 || ThreadLocalRandom.current().nextInt(0, 2) == 1 ? AttackAction.Sword1H: AttackAction.Sword2H;
    }


    @Override
    protected Logger logger() {
        return log;
    }

    @Override
    public KungFu duplicate() {
        return new SwordKungFu(name(), exp(), getParameters());
    }
}
