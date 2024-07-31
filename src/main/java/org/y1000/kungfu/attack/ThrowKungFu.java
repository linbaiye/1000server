package org.y1000.kungfu.attack;

import lombok.Builder;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.State;
import org.y1000.item.ItemType;
import org.y1000.kungfu.KungFu;

@Slf4j
public final class ThrowKungFu extends AbstractRangedKungFu {

    @Builder
    public ThrowKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    @Override
    protected ItemType getAmmoType() {
        return ItemType.KNIFE;
    }

    @Override
    public State randomAttackState() {
        return State.THROW;
    }


    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.THROW;
    }


    @Override
    protected Logger logger() {
        return log;
    }

    @Override
    public KungFu duplicate() {
        return new ThrowKungFu(name(), 0, getParameters());
    }
}
