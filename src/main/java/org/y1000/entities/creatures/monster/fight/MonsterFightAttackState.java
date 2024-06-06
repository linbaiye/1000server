package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.AbstractMonsterState;
import org.y1000.entities.creatures.monster.PassiveMonster;

public class MonsterFightAttackState extends AbstractMonsterState {
    public MonsterFightAttackState(int totalMillis) {
        super(totalMillis, State.ATTACK);
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
        monster.nextHuntMove();
    }

    public static MonsterFightAttackState of(PassiveMonster monster) {
        return new MonsterFightAttackState(monster.getStateMillis(State.ATTACK));
    }
}
