package org.y1000.message.serverevent;

import org.y1000.entities.Entity;
import org.y1000.message.AbstractServerMessage;

public abstract class AbstractSerializableEntityEvent extends AbstractServerMessage
        implements EntityEvent {

    private final Entity source;

    public AbstractSerializableEntityEvent(Entity source) {
        this.source = source;
    }

    @Override
    public Entity source() {
        return source;
    }
}
