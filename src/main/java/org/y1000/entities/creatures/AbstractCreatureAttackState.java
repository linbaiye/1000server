package org.y1000.entities.creatures;

public abstract class AbstractCreatureAttackState<C extends Creature> extends AbstractCreateState<C> {

    protected AbstractCreatureAttackState(int lengthMillis) {
        super(lengthMillis);
    }


    @Override
    public State stateEnum() {
        return State.ATTACK;
    }
}
