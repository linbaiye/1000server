package org.y1000.entities.creatures;

public abstract class AbstractCreatureIdleState<E extends Creature> extends AbstractCreateState<E> {

    public AbstractCreatureIdleState(int length) {
        super(length);
    }

    @Override
    public State stateEnum() {
        return State.IDLE;
    }
}
