package org.y1000.entities.objects;

import org.y1000.entities.Entity;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventVisitor;

public final class DynamicObjectDieEvent implements EntityEvent  {

    private final DynamicObject source;

    public DynamicObject object() {
        return source;
    }

    public DynamicObjectDieEvent(DynamicObject source) {
        this.source = source;
    }

    @Override
    public Entity source() {
        return source;
    }

    @Override
    public void accept(EntityEventVisitor visitor) {

    }
}
