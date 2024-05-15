package org.y1000.entities.players.kungfu.attack.unnamed;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.kungfu.attack.AbstractAttackKungFu;
import org.y1000.entities.players.kungfu.attack.AttackKungFuType;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Getter
@SuperBuilder
public final class UnnamedQuanFa extends AbstractAttackKungFu {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Override
    public String name() {
        return "无名拳法";
    }


    public static UnnamedQuanFa start() {
         return UnnamedQuanFa.builder()
                .bodyDamage(163)
                .level(100)
                .attackSpeed(40)
                .bodyArmor(32)
                .build();
    }

    @Override
    public State randomAttackState() {
        return level() < 50 || RANDOM.nextInt() % 2 == 1 ? State.FIST : State.KICK;
    }

    @Override
    public int attackActionMillis(State state) {
        assert state == State.FIST || state == State.KICK;
        return state == State.FIST ? AttackKungFuType.QUANFA.below50Millis() : AttackKungFuType.QUANFA.above50Millis();
    }

    @Override
    public AttackKungFuType getType() {
        return AttackKungFuType.QUANFA;
    }
}
