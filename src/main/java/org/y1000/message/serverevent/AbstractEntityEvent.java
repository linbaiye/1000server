package org.y1000.message.serverevent;

import org.y1000.entities.Entity;
import org.y1000.message.AbstractServerMessage;

public abstract class AbstractEntityEvent extends AbstractServerMessage
        implements EntityEvent {

    private final Entity source;

    public AbstractEntityEvent(Entity source) {
        this.source = source;
    }

    @Override
    public Entity source() {
        return source;
    }

}
