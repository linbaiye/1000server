package org.y1000.kungfu.attack;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.State;

@Slf4j
@SuperBuilder
public class ThrowKungFu extends AbstractRangedKungFu {

    @Override
    public State randomAttackState() {
        return State.THROW;
    }


    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.THROW;
    }

    public static ThrowKungFu unnamed() {
        return ThrowKungFu.builder()
                .bodyDamage(1)
                .attackSpeed(100)
                .name("无名投法")
                .level(100)
                .bodyArmor(0)
                .recovery(100)
                .build();
    }

    @Override
    protected Logger logger() {
        return log;
    }
}
