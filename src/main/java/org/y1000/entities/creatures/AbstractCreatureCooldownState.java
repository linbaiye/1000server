package org.y1000.entities.creatures;

public abstract class AbstractCreatureCooldownState <E extends Creature> extends AbstractCreateState<E>{

    @Override
    public State stateEnum() {
        return State.COOLDOWN;
    }

}
