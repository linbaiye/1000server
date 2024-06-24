package org.y1000.entities.creatures.monster;

import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.State;

public final class MonsterDieState extends AbstractMonsterState {

    public MonsterDieState(int totalMillis) {
        super(totalMillis, State.DIE);
    }

    @Override
    protected void nextMove(AbstractMonster monster) {
        monster.emitEvent(new RemoveEntityEvent(monster));
        monster.realmMap().free(monster);
    }

    @Override
    public boolean attackable() {
        return false;
    }

    public static MonsterDieState die(AbstractMonster monster) {
        return new MonsterDieState(monster.getStateMillis(State.DIE) + 8000);
    }
}
