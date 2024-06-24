package org.y1000.entities;

import org.y1000.message.serverevent.EntityEvent;
import org.y1000.message.serverevent.EntityEventListener;

public abstract class AbstractPhysicalEntity implements PhysicalEntity {
    private final long id;
    private final EventListeners eventListeners;

    protected AbstractPhysicalEntity(long id) {
        this.id = id;
        this.eventListeners = new EventListeners();
    }

    public void emitEvent(EntityEvent event) {
        eventListeners.notifyListeners(event);
    }

    @Override
    public void registerEventListener(EntityEventListener listener) {
        eventListeners.register(listener);
    }

    @Override
    public void deregisterEventListener(EntityEventListener listener) {
        eventListeners.deregister(listener);
    }

    @Override
    public long id() {
        return id;
    }
}
