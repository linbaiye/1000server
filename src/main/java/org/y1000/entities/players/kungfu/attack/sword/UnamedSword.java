package org.y1000.entities.players.kungfu.attack.sword;

import lombok.experimental.SuperBuilder;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.kungfu.attack.AbstractAttackKungFu;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;

import java.util.concurrent.ThreadLocalRandom;

@SuperBuilder
public class UnamedSword extends AbstractAttackKungFu  {

    @Override
    public String name() {
        return "无名剑法";
    }

    @Override
    public State randomAttackState() {
        return level() < 50 || ThreadLocalRandom.current().nextInt() % 2 == 1 ? State.SWORD : State.SWORD2H;
    }

    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.SWORD;
    }
}
