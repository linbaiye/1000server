package org.y1000.entities.creatures.monster;


import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.fight.MonsterCooldownState;

public final class MonsterAttackState extends AbstractMonsterState {

    private final Creature target;

    public MonsterAttackState(int lengthMillis, Creature target) {
        super(lengthMillis, State.ATTACK);
        this.target = target;
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
        monster.attack(target);
    }

    @Override
    public void afterHurt(PassiveMonster monster, Creature attacker) {
        monster.changeState(new MonsterCooldownState(monster.cooldown(), target));
    }

    public static MonsterAttackState attack(PassiveMonster monster, Creature target) {
        return new MonsterAttackState(monster.getStateMillis(State.ATTACK), target);
    }
}
