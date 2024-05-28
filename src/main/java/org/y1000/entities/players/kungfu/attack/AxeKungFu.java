package org.y1000.entities.players.kungfu.attack;

import lombok.experimental.SuperBuilder;
import org.y1000.entities.creatures.State;

@SuperBuilder
public final class AxeKungFu extends AbstractAttackKungFu{

    @Override
    public State randomAttackState() {
        return State.AXE;
    }

    @Override
    public boolean hasState(State state) {
        return state == State.AXE;
    }

    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.AXE;
    }


    public static AxeKungFu unnamed() {
        return AxeKungFu.builder()
                .name("无名锤法")
                .level(100)
                .recovery(100)
                .attackSpeed(100)
                .bodyArmor(1)
                .bodyDamage(1)
                .build();
    }

}
