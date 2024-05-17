package org.y1000.entities.creatures;

import lombok.extern.slf4j.Slf4j;

@Slf4j
final class PassiveMonsterHurtState extends AbstractCreatureHurtState<PassiveMonster>
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

    @FunctionalInterface
    public interface AfterHurtAction<C> {
        void apply(C c, Creature attacker);
    }
}
