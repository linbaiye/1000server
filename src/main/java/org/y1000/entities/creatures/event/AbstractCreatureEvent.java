package org.y1000.entities.creatures.event;

import org.y1000.entities.AttackableEntity;
import org.y1000.entities.creatures.Creature;
import org.y1000.message.AbstractClientMessage;
import org.y1000.event.IEntityEvent;

public abstract class AbstractCreatureEvent extends AbstractClientMessage implements IEntityEvent {
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
