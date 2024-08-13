package org.y1000.entities.objects;


import java.util.Set;

/**
 * A dynamic object that is driven by other objects.
 */
public interface EventDrivenDynamicObject extends DynamicObject {

    void subscribe(Set<DynamicObject> dynamicObjects);
}
