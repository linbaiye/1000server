package org.y1000.entities.creatures;

import org.y1000.entities.players.State;

public abstract class AbstractCreatureIdleState<E extends Creature> extends AbstractCreateState<E> {

    // How long does this state last.
    private final int lengthMillis;

    public AbstractCreatureIdleState(int length) {
        this.lengthMillis = length;
    }

    @Override
    public State stateEnum() {
        return State.IDLE;
    }

    protected boolean resetIfElapsedLength(int deltaMillis) {
        if (elapsedMillis() >= lengthMillis) {
            resetElapsedMillis();
        }
        elapse(deltaMillis);
        return elapsedMillis() >= lengthMillis;
    }

}
