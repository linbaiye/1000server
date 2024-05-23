package org.y1000.entities.players.kungfu.attack;

import lombok.experimental.SuperBuilder;
import org.y1000.entities.creatures.State;

import java.util.concurrent.ThreadLocalRandom;

@SuperBuilder
public final class BladeKungFu extends AbstractAttackKungFu {

    @Override
    public State randomAttackState() {
        return level() < 50 || ThreadLocalRandom.current().nextInt() % 2 == 1 ? State.BLADE : State.BLADE2H;
    }

    @Override
    public boolean hasState(State state) {
        return state == State.BLADE || state == State.BLADE2H;
    }

    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.BLADE;
    }

    public static BladeKungFu unnamed() {
        return BladeKungFu.builder()
                .name("无名刀法")
                .bodyArmor(1)
                .bodyDamage(1)
                .attackSpeed(50)
                .recovery(50)
                .build();
    }

}
