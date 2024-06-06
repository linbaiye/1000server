package org.y1000.kungfu.attack;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.State;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SuperBuilder
public final class SwordKungFu extends AbstractMeleeKungFu {

    @Override
    public State randomAttackState() {
        return level() < 50 || ThreadLocalRandom.current().nextInt() % 2 == 1 ? State.SWORD : State.SWORD2H;
        //return level() < 5000 || ThreadLocalRandom.current().nextInt() % 2 == 1 ? State.SWORD : State.SWORD2H;
    }


    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.SWORD;
    }

    public static SwordKungFu unnamed() {
        return SwordKungFu.builder()
                .name("无名剑法")
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
