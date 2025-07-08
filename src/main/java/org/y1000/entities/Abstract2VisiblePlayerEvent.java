package org.y1000.entities;

import org.y1000.event.EntityEvent;

public class Abstract2VisiblePlayerEvent implements EntityEvent<Entity> {
    @Override
    public Entity source() {
        return null;
    }
}
