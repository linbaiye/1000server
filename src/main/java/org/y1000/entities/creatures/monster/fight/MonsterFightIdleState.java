package org.y1000.entities.creatures.monster.fight;

import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.AbstractMonsterIdleState;
import org.y1000.entities.creatures.monster.PassiveMonster;
import org.y1000.util.Coordinate;

public class MonsterFightIdleState extends AbstractMonsterIdleState {
    public MonsterFightIdleState(int totalMillis, Coordinate from) {
        super(totalMillis, from);
    }

    @Override
    protected void nextMove(PassiveMonster monster) {
        monster.changeState(MonsterFightFrozenState.next(monster, getFrom()));
    }

    public static MonsterFightIdleState start(PassiveMonster monster) {
        return new MonsterFightIdleState(monster.getStateMillis(State.IDLE), new Coordinate(0, 0));
    }

    public static MonsterFightIdleState nextStep(PassiveMonster monster, Coordinate from) {
        return new MonsterFightIdleState(monster.getStateMillis(State.IDLE), from);
    }
}
