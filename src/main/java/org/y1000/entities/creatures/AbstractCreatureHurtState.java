package org.y1000.entities.creatures;

import lombok.Getter;

@Getter
public abstract class AbstractCreatureHurtState<C extends Creature> extends AbstractCreateState<C>{


    private final Creature attacker;

    protected AbstractCreatureHurtState(int totalMillis,  Creature attacker) {
        super(totalMillis);
        this.attacker = attacker;
    }

    protected abstract void recovery(C c);

    @Override
    public void update(C c, int delta) {
        if (elapse(delta)) {
            recovery(c);
        }
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public State stateEnum() {
        return State.HURT;
    }
}
