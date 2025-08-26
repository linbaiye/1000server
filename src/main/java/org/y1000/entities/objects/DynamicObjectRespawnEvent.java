package org.y1000.entities.objects;

import org.y1000.realm.DynamicObjectEventHandler;

public record DynamicObjectRespawnEvent(DynamicObject source) implements DynamicObjectEvent {

    @Override
    public void accept(DynamicObjectEventHandler handler) {
        handler.onRespawn(source);
    }
}
