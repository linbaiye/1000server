package org.y1000.kungfu.attack;

import lombok.experimental.SuperBuilder;
import org.y1000.entities.creatures.State;

@SuperBuilder
public class ThrowKungFu extends AbstractAttackKungFu {

    @Override
    public State randomAttackState() {
        return State.THROW;
    }

    @Override
    public boolean hasState(State state) {
        return state == State.THROW;
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
}
