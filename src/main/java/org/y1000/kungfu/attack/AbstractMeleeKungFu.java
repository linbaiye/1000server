package org.y1000.kungfu.attack;

import org.slf4j.Logger;
import org.y1000.entities.players.PlayerImpl;

public abstract class AbstractMeleeKungFu extends AbstractAttackKungFu {

    public AbstractMeleeKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    protected abstract Logger logger();

    @Override
    protected boolean useResources(PlayerImpl player) {
        return useAttributeResources(player);
    }

    @Override
    public boolean isRanged() {
        return false;
    }
}
