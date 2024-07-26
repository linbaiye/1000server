package org.y1000.entities;

import org.y1000.event.EntityEvent;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.event.EntityEventListener;
import org.y1000.util.Coordinate;


public interface ActiveEntity extends Entity {

    void update(int delta);

    void emitEvent(EntityEvent event);

    void registerEventListener(EntityEventListener listener);

    void deregisterEventListener(EntityEventListener listener);
}
