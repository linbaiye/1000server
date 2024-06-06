package org.y1000.kungfu.attack;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.State;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SuperBuilder
public final class QuanfaKungFu extends AbstractMeleeKungFu {

    @Override
    public State randomAttackState() {
        return level() < 5000 || ThreadLocalRandom.current().nextInt() % 2 == 1 ? State.FIST: State.KICK;
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

    @Override
    protected Logger logger() {
        return log;
    }
}
