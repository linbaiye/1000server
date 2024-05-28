package org.y1000.entities.players.kungfu.attack;

import lombok.experimental.SuperBuilder;
import org.y1000.entities.creatures.State;

@SuperBuilder
public final class SpearKungFu extends AbstractAttackKungFu{

    @Override
    public State randomAttackState() {
        return State.SPEAR;
    }

    @Override
    public boolean hasState(State state) {
        return State.SPEAR == state;
    }
    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.SPEAR;
    }

    public static SpearKungFu unnamed() {
        return SpearKungFu.builder()
                .name("无名枪术")
                .level(100)
                .recovery(100)
                .attackSpeed(100)
                .bodyArmor(1)
                .bodyDamage(1)
                .build();
    }
}
