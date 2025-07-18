package org.y1000.entities.objects;

import org.y1000.entities.Entity;
import org.y1000.event.IEntityEvent;
import org.y1000.event.EntityEventVisitor;

public final class DynamicObjectDieEvent implements IEntityEvent {

    private final IDynamicObject source;

    public IDynamicObject object() {
        return source;
    }

    public DynamicObjectDieEvent(IDynamicObject source) {
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
