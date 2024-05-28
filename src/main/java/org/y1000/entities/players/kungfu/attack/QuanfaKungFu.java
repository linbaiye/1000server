package org.y1000.entities.players.kungfu.attack;

import lombok.experimental.SuperBuilder;
import org.y1000.entities.creatures.State;

import java.util.concurrent.ThreadLocalRandom;

@SuperBuilder
public final class QuanfaKungFu extends AbstractAttackKungFu {

    @Override
    public State randomAttackState() {
        return level() < 50 || ThreadLocalRandom.current().nextInt() % 2 == 1 ? State.FIST: State.KICK;
    }

    @Override
    public boolean hasState(State state) {
        return state == State.FIST || state == State.KICK;
    }

    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.QUANFA;
    }

    public static QuanfaKungFu unnamed() {
        return QuanfaKungFu.builder()
                .name("无名拳法")
                .level(100)
                .recovery(100)
                .attackSpeed(100)
                .bodyArmor(1)
                .bodyDamage(1)
                .build();
    }
}
