package org.y1000.kungfu.attack;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.kungfu.KungFu;

@Slf4j
public final class SpearKungFu extends AbstractMeleeKungFu {

    @Builder
    public SpearKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    @Override
    public OldPlayerStateEnum randomAttackState() {
        return OldPlayerStateEnum.SPEAR;
    }


    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.SPEAR;
    }

    @Override
    protected Logger logger() {
        return log;
    }

    @Override
    public KungFu duplicate() {
        return new SpearKungFu(name(), 0, getParameters());
    }
}
