package org.y1000.kungfu.attack;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.entities.players.AttackAction;
import org.y1000.item.ItemType;
import org.y1000.kungfu.KungFu;

@Slf4j
public final class BowKungFu extends AbstractRangedKungFu {

    @Builder
    public BowKungFu(String name, int exp, AttackKungFuParameters parameters) {
        super(name, exp, parameters);
    }

    @Override
    public ItemType getAmmoType() {
        return ItemType.ARROW;
    }

    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.BOW;
    }

    @Override
    public AttackAction computeAttackAction() {
        return AttackAction.Bow;
    }


    @Override
    protected Logger logger() {
        return log;
    }

    @Override
    public KungFu duplicate() {
        return new BowKungFu(name(), exp(), getParameters());
    }
}
