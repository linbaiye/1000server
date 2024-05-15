package org.y1000.entities.creatures;

public abstract class AbstractMonsterState extends AbstractCreateState<PassiveMonster>
        implements MonsterState<PassiveMonster> {

    private final State stat;

    public AbstractMonsterState(int totalMillis, State stat) {
        super(totalMillis);
        this.stat = stat;
    }

    @Override
    public State stateEnum() {
        return stat;
    }

    protected abstract void nextMove(PassiveMonster monster);

    @Override
    public void update(PassiveMonster monster, int delta) {
        if (elapse(delta)) {
            nextMove(monster);
        }
    }
}
