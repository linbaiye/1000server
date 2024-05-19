package org.y1000.entities.players.kungfu.attack.bow;

import lombok.experimental.SuperBuilder;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.kungfu.attack.AbstractAttackKungFu;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;

@SuperBuilder
public final class UnnamedBow extends AbstractAttackKungFu  {

    @Override
    public String name() {
        return "无名弓术";
    }

    @Override
    public State randomAttackState() {
        return State.BOW;
    }

    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.BOW;
    }

    @Override
    public boolean isRanged() {
        return true;
    }
}
