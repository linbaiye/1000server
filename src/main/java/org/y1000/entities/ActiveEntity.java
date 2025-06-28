package org.y1000.entities;

import org.y1000.event.EntityEvent;
import org.y1000.event.IEntityEvent;
import org.y1000.event.EntityEventListener;


public interface ActiveEntity extends Entity {

    void update(int delta);

    void emitEvent(EntityEvent event);

    void registerEventListener(EntityEventListener listener);

    void deregisterEventListener(EntityEventListener listener);

    void clearListeners();
}
