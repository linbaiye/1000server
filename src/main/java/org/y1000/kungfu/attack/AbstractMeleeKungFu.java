package org.y1000.kungfu.attack;

import org.slf4j.Logger;

public abstract class AbstractMeleeKungFu extends AbstractAttackKungFu {

    public AbstractMeleeKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    protected abstract Logger logger();

    @Override
    public boolean isRanged() {
        return false;
    }
}
