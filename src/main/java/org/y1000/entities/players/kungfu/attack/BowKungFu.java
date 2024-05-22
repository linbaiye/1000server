package org.y1000.entities.players.kungfu.attack;

import lombok.experimental.SuperBuilder;
import org.y1000.entities.creatures.State;

@SuperBuilder
public final class BowKungFu extends AbstractAttackKungFu  {

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
}
