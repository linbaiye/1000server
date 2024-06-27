package org.y1000.entities.creatures;

import org.y1000.entities.creatures.event.CreatureDieEvent;

public abstract class AbstractCreatureDieState<C extends Creature> extends AbstractCreatureState<C> {
    public AbstractCreatureDieState(int totalMillis) {
        super(totalMillis);
    }

    @Override
    public State stateEnum() {
        return State.DIE;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public void update(C creature, int delta) {
        if (elapse(delta)) {
            creature.emitEvent(new CreatureDieEvent(creature));
            creature.realmMap().free(creature);
        }
    }
}
