package org.y1000.realm;

import org.y1000.entities.objects.DynamicObject;

public interface DynamicObjectManager extends ActiveEntityManager<DynamicObject> {

    DynamicObjectManager EMPTY = EmptyDynamicObjectmanager.INSTANCE;
    
    void init();

}
