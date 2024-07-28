package org.y1000.message.serverevent;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Entity;
import org.y1000.event.EntityEvent;
import org.y1000.message.AbstractServerMessage;

public abstract class Abstract2ClientEntityEvent extends AbstractServerMessage
        implements EntityEvent {

    private final Entity source;

    public Abstract2ClientEntityEvent(Entity source) {
        this.source = source;
    }

    @Override
    public Entity source() {
        return source;
    }
}
