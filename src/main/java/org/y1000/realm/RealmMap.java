package org.y1000.realm;


import org.y1000.entities.Entity;
import org.y1000.entities.objects.DynamicObject;
import org.y1000.util.Coordinate;

import java.util.Optional;
import java.util.Set;

public interface RealmMap {

    boolean movable(Coordinate coordinate);

    void occupy(Entity entity);

    void free(Entity creature);

    void occupy(DynamicObject dynamicObject);

    void free(DynamicObject dynamicObject);

    String name();

    static Optional<RealmMap> Load(String name) {
        return RealmMapV2Impl.read(name);
    }
}
