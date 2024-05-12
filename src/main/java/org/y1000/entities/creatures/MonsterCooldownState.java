package org.y1000.entities.creatures;

import org.y1000.entities.creatures.event.CreatureAttackEvent;

public final class MonsterCooldownState extends AbstractMonsterState {

    private final Creature attacker;

    public MonsterCooldownState(int totalMillis, Creature attacker) {
        super(totalMillis, State.IDLE);
        this.attacker = attacker;
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
        monster.changeState(MonsterAttackState.attack(monster, attacker));
        monster.emitEvent(CreatureAttackEvent.ofMonster(monster));
    }

    public static MonsterCooldownState of(PassiveMonster monster, Creature attacker) {
        return new MonsterCooldownState(Math.max(monster.recoveryCooldown(), monster.attackCooldown()), attacker);
    }
}
