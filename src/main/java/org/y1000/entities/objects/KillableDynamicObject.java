package org.y1000.entities.objects;

import lombok.Builder;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.util.Objects;

public final class KillableDynamicObject extends AbstractSimpleKillableDynamicObject{
    @Builder
    public KillableDynamicObject(long id, Coordinate coordinate, RealmMap realmMap, DynamicObjectSdb dynamicObjectSdb, String idName) {
        super(id, coordinate, realmMap, dynamicObjectSdb, idName);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KillableDynamicObject object)) return false;
        return Objects.equals(id(), object.id());
    }


    @Override
    public int hashCode() {
        return Objects.hash(id());
    }
}
