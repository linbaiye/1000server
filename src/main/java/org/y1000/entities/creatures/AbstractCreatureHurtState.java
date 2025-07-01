package org.y1000.entities.creatures;

import lombok.Getter;

@Getter
public abstract class AbstractCreatureHurtState<C extends Creature> extends IAbstractCreatureState<C> {


    protected AbstractCreatureHurtState(int totalMillis) {
        super(totalMillis);
    }

    protected abstract void recovery(C c);

    @Override
    public void update(C c, int delta) {
        if (elapse(delta)) {
            recovery(c);
        }
    }
}
