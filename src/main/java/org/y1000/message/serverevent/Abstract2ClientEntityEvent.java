package org.y1000.message.serverevent;

import org.y1000.entities.Entity;
import org.y1000.event.IEntityEvent;
import org.y1000.message.AbstractClientMessage;

public abstract class Abstract2ClientEntityEvent extends AbstractClientMessage
        implements IEntityEvent {

    private final Entity source;

    public Abstract2ClientEntityEvent(Entity source) {
        this.source = source;
    }

    @Override
    public Entity source() {
        return source;
    }
}
