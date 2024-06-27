package org.y1000.entities.creatures;


public abstract class AbstractNpcState<C extends Creature> extends AbstractCreatureState<C> {

    private final State stat;

    public AbstractNpcState(int totalMillis, State stat) {
        super(totalMillis);
        this.stat = stat;
    }

    @Override
    public State stateEnum() {
        return stat;
    }

    protected abstract void nextMove(C creature);
    @Override
    public void update(C creature, int delta) {
        if (elapse(delta)) {
            nextMove(creature);
        }
    }
}
