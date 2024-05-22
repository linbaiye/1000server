package org.y1000.entities.players.kungfu.attack;

import lombok.experimental.SuperBuilder;
import org.y1000.entities.creatures.State;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@SuperBuilder
public final class SwordKungFu extends AbstractAttackKungFu {

    @Override
    public State randomAttackState() {
        return level() < 50 || ThreadLocalRandom.current().nextInt() % 2 == 1 ? State.SWORD : State.SWORD2H;
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
                .attackSpeed(50)
                .recovery(50)
                .build();
    }
}
