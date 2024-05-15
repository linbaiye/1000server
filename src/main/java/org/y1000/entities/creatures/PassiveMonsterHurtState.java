package org.y1000.entities.creatures;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PassiveMonsterHurtState extends AbstractCreatureHurtState<PassiveMonster>
        implements MonsterState<PassiveMonster> {

    private final AfterHurtAction<PassiveMonster> action;

    public PassiveMonsterHurtState(Creature attacker, int totalMillis, AfterHurtAction<PassiveMonster> after) {
        super(totalMillis, attacker);
        action = after;
    }

    @Override
    protected void recovery(PassiveMonster monster) {
        action.apply(monster, getAttacker());
    }

    @FunctionalInterface
    public interface AfterHurtAction<C> {
        void apply(C c, Creature attacker);
    }
}
