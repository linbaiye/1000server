package org.y1000.entities.objects;

public abstract class AbstractDynamicObjectEvent implements DynamicObjectEvent {

    private final DynamicObject source;

    protected AbstractDynamicObjectEvent(DynamicObject source) {
        this.source = source;
    }

    @Override
    public DynamicObject source() {
        return source;
    }
}
