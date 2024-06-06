package org.y1000.kungfu.attack;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.State;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SuperBuilder
public final class BladeKungFu extends AbstractMeleeKungFu {

    @Override
    public State randomAttackState() {
        return level() < 5000 || ThreadLocalRandom.current().nextInt() % 2 == 1 ? State.BLADE : State.BLADE2H;
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
                .level(100)
                .attackSpeed(50)
                .recovery(50)
                .build();
    }

    @Override
    protected Logger logger() {
        return log;
    }
}
