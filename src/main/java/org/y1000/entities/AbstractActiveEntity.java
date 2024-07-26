package org.y1000.entities;

import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventListener;

public abstract class AbstractActiveEntity implements ActiveEntity {
    private final long id;
    private final EventListeners eventListeners;

    protected AbstractActiveEntity(long id) {
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
