package org.y1000.kungfu.attack;

import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;

@SuperBuilder
public abstract class AbstractMeleeKungFu extends AbstractAttackKungFu {

    protected abstract Logger logger();

    @Override
    public boolean isRanged() {
        return false;
    }
}
