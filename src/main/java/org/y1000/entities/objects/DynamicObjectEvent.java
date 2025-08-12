package org.y1000.entities.objects;

import org.y1000.entities.TypedEntityEvent;
import org.y1000.realm.DynamicObjectEventHandler;

public interface DynamicObjectEvent extends TypedEntityEvent<DynamicObject> {
    void accept(DynamicObjectEventHandler handler);
}
