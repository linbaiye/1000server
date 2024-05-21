package org.y1000.message.serverevent;

import org.y1000.entities.PhysicalEntity;
import org.y1000.message.AbstractServerMessage;

public abstract class Abstract2ClientEntityEvent extends AbstractServerMessage
        implements EntityEvent {

    private final PhysicalEntity source;

    public Abstract2ClientEntityEvent(PhysicalEntity source) {
        this.source = source;
    }

    @Override
    public PhysicalEntity source() {
        return source;
    }
}
