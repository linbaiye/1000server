package org.y1000.entities.creatures;

public abstract class AbstractCreatureCooldownState <E extends Creature> extends AbstractCreateState<E>{

    public AbstractCreatureCooldownState(int totalMillis) {
        super(totalMillis);
    }

    @Override
    public State stateEnum() {
        return State.COOLDOWN;
    }

}
