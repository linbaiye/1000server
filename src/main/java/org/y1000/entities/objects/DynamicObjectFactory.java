package org.y1000.entities.objects;

import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

public interface DynamicObjectFactory {

    TriggerDynamicObject createDynamicObject(String name, long id, RealmMap realmMap, Coordinate coordinate);

}
