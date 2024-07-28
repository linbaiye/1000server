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
    public DynamicObject createDynamicObject(String name,
                                                    long id,
                                                    RealmMap realmMap,
                                                    Coordinate coordinate) {
        Validate.notNull(name);
        Validate.notNull(realmMap);
        Validate.notNull(coordinate);
        DynamicObjectType kind = dynamicObjectSdb.getKind(name);
        if (kind == DynamicObjectType.TRIGGER) {
            return TriggerDynamicObject.builder()
                    .id(id)
                    .idName(name)
                    .realmMap(realmMap)
                    .coordinate(coordinate)
                    .dynamicObjectSdb(dynamicObjectSdb)
                    .build();
        } else if (kind == DynamicObjectType.KILLABLE) {
            return KillableDynamicObject.builder()
                    .id(id)
                    .coordinate(coordinate)
                    .idName(name)
                    .realmMap(realmMap)
                    .dynamicObjectSdb(dynamicObjectSdb)
                    .build();
        } else if (kind == DynamicObjectType.YAOHUA) {
            return Yaohua.builder()
                    .id(id)
                    .coordinate(coordinate)
                    .idName(name)
                    .realmMap(realmMap)
                    .dynamicObjectSdb(dynamicObjectSdb)
                    .build();
        }
        log.error("Unable to create dynamic object : " + name);
        return null;
    }
}
