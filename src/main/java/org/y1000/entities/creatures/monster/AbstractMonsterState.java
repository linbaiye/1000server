package org.y1000.entities.creatures.monster;

import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;

public abstract class AbstractMonsterState extends AbstractCreateState<AbstractMonster>
        implements MonsterState<AbstractMonster> {

    private final State stat;

    public AbstractMonsterState(int totalMillis, State stat) {
        super(totalMillis);
        this.stat = stat;
    }

    @Override
    public State stateEnum() {
        return stat;
    }

    protected abstract void nextMove(AbstractMonster monster);

    @Override
    public void update(AbstractMonster monster, int delta) {
        if (elapse(delta)) {
            nextMove(monster);
        }
    }
}
