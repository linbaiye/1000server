package org.y1000.kungfu.attack;



public abstract class AbstractRangedKungFu extends AbstractAttackKungFu {

    public AbstractRangedKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    @Override
    public boolean isRanged() {
        return true;
    }
}
