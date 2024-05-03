package org.y1000.entities.creatures;

import org.y1000.entities.players.State;

public abstract class AbstractCreatureHurtState<C extends Creature> extends AbstractCreateState<C>{

    @Override
    public State stateEnum() {
        return State.HURT;
    }
}
