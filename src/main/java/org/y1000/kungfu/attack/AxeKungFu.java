package org.y1000.kungfu.attack;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.State;
import org.y1000.kungfu.KungFuType;

@Slf4j
@SuperBuilder
public final class AxeKungFu extends AbstractMeleeKungFu {

    @Override
    public State randomAttackState() {
        return State.AXE;
    }


    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.AXE;
    }


    public static AxeKungFu unnamed() {
        return AxeKungFu.builder()
                .name("无名槌法")
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
