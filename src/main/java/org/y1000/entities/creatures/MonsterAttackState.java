package org.y1000.entities.creatures;


public final class MonsterAttackState extends AbstractMonsterState {

    private final Creature target;

    public MonsterAttackState(int lengthMillis, Creature target) {
        super(lengthMillis, State.ATTACK);
        this.target = target;
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
        monster.retaliate(target);
    }

    @Override
    public void afterHurt(PassiveMonster monster, Creature attacker) {
        monster.changeState(new MonsterCooldownState(monster.cooldown(), target));
    }

    public static MonsterAttackState attack(PassiveMonster monster, Creature target) {
        return new MonsterAttackState(monster.getStateMillis(State.ATTACK), target);
    }
}
