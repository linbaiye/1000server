package org.y1000.kungfu.attack;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.State;

@Slf4j
@SuperBuilder
public final class BowKungFu extends AbstractRangedKungFu {

    @Override
    public State randomAttackState() {
        return State.BOW;
    }


    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.BOW;
    }


    public static BowKungFu unnamed() {
        return BowKungFu.builder()
                .name("无名弓术")
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
