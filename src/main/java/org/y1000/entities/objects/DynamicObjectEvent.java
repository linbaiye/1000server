package org.y1000.entities.objects;

import org.y1000.event.EntityEvent;
import org.y1000.realm.DynamicObjectEventHandler;

public interface DynamicObjectEvent extends EntityEvent<DynamicObject> {
    void accept(DynamicObjectEventHandler handler);
}
