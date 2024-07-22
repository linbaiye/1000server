package org.y1000.entities.objects;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

@Slf4j
public final class DynamicObjectFactoryImpl implements DynamicObjectFactory {
    private final DynamicObjectSdb dynamicObjectSdb;

    public DynamicObjectFactoryImpl(DynamicObjectSdb dynamicObjectSdb) {
        this.dynamicObjectSdb = dynamicObjectSdb;
    }

    @Override
    public TriggerDynamicObject createDynamicObject(String name,
                                                    long id,
                                                    RealmMap realmMap,
                                                    Coordinate coordinate) {
        Validate.notNull(name);
        Validate.notNull(realmMap);
        Validate.notNull(coordinate);
        DynamicObjectType kind = dynamicObjectSdb.getKind(name);
        if (kind != DynamicObjectType.TRIGGER) {
            log.error("{} is not a trigger object.", name);
            throw new IllegalArgumentException("Invalid type.");
        }
        return TriggerDynamicObject.builder()
                .id(id)
                .idName(name)
                .realmMap(realmMap)
                .coordinate(coordinate)
                .dynamicObjectSdb(dynamicObjectSdb)
                .build();
    }
}
