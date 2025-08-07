package org.y1000.entities;

import org.y1000.event.TypedEntityEvent;

public class Abstract2VisiblePlayerEvent implements TypedEntityEvent<Entity> {
    @Override
    public Entity source() {
        return null;
    }
}
