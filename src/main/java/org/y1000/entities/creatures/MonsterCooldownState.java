package org.y1000.entities.creatures;


public final class MonsterCooldownState extends AbstractMonsterState {

    private final Creature attacker;

    public MonsterCooldownState(int totalMillis, Creature attacker) {
        super(totalMillis, State.COOLDOWN);
        this.attacker = attacker;
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
        monster.retaliate(attacker);
    }

    public static MonsterCooldownState of(PassiveMonster monster, Creature attacker) {
        return new MonsterCooldownState(Math.max(monster.recoveryCooldown(), monster.attackCooldown()), attacker);
    }
}
