package org.y1000.entities.creatures;

public abstract class AbstractCreatureHurtState<C extends Creature> extends AbstractCreateState<C>{

    public AbstractCreatureHurtState(int totalMillis) {
        super(totalMillis);
    }

    @Override
    public State stateEnum() {
        return State.HURT;
    }
}
