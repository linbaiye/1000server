package org.y1000.message.serverevent;

import org.y1000.entities.ActiveEntity;
import org.y1000.event.EntityEvent;
import org.y1000.message.AbstractServerMessage;

public abstract class Abstract2ClientEntityEvent extends AbstractServerMessage
        implements EntityEvent {

    private final ActiveEntity source;

    public Abstract2ClientEntityEvent(ActiveEntity source) {
        this.source = source;
    }

    @Override
    public ActiveEntity source() {
        return source;
    }
}
