package org.y1000.entities.creatures.monster;

import org.y1000.entities.creatures.AbstractCreatureDieState;
import org.y1000.entities.creatures.State;

public final class MonsterDieState extends AbstractCreatureDieState<AbstractMonster> implements
        MonsterState<AbstractMonster> {

    public MonsterDieState(int totalMillis) {
        super(totalMillis);
    }

    public static MonsterDieState die(AbstractMonster monster) {
        return new MonsterDieState(monster.getStateMillis(State.DIE) + 8000);
    }
}
