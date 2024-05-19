package org.y1000.entities.creatures.monster;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.AbstractCreatureHurtState;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.State;

@Slf4j
public final class PassiveMonsterHurtState extends AbstractCreatureHurtState<PassiveMonster>
        implements MonsterState<PassiveMonster> {

    private final AfterHurtAction<PassiveMonster> action;

    private final Creature attacker;

    public PassiveMonsterHurtState(Creature attacker, int totalMillis, AfterHurtAction<PassiveMonster> after) {
        super(totalMillis);
        action = after;
        this.attacker = attacker;
    }

    @Override
    protected void recovery(PassiveMonster monster) {
        action.apply(monster, attacker);
    }

    @Override
    public void afterHurt(PassiveMonster passiveMonster, Creature attacker) {
        reset();
        passiveMonster.changeState(this);
    }

    @FunctionalInterface
    public interface AfterHurtAction<C> {
        void apply(C c, Creature attacker);
    }
}
