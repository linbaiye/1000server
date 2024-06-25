package org.y1000.entities.creatures.event;

import org.y1000.entities.AttackableEntity;
import org.y1000.entities.creatures.Creature;
import org.y1000.message.AbstractServerMessage;
import org.y1000.event.EntityEvent;

public abstract class AbstractCreatureEvent extends AbstractServerMessage implements EntityEvent {
    private final Creature source;

    protected AbstractCreatureEvent(Creature source) {
        this.source = source;
    }


    public Creature creature() {
        return source;
    }

    @Override
    public AttackableEntity source() {
        return source;
    }
}
