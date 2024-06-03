package org.y1000.kungfu.attack;

import lombok.experimental.SuperBuilder;


@SuperBuilder
public abstract class AbstractRangedKungFu extends AbstractAttackKungFu {

    @Override
    public boolean isRanged() {
        return true;
    }
}
