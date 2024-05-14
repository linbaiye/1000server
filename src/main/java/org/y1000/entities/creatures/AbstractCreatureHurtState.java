package org.y1000.entities.creatures;

import lombok.Getter;

public abstract class AbstractCreatureHurtState<C extends Creature> extends AbstractCreateState<C>{

    private final AfterHurtAction<C> action;

    @Getter
    private final Creature attacker;

    protected AbstractCreatureHurtState(int totalMillis, AfterHurtAction<C> action, Creature attacker) {
        super(totalMillis);
        this.action = action;
        this.attacker = attacker;
    }


    @FunctionalInterface
    public interface AfterHurtAction<C> {
        void apply(C c, Creature attacker);
    }

    @Override
    public void update(C c, int delta) {
        if (elapse(delta)) {
            action.apply(c, attacker);
        }
    }

    @Override
    public State stateEnum() {
        return State.HURT;
    }
}
