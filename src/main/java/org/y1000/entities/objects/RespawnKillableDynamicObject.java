package org.y1000.entities.objects;

import lombok.Builder;
import org.y1000.realm.RealmMap;
import org.y1000.sdb.DynamicObjectSdb;
import org.y1000.util.Coordinate;

import java.util.Objects;

public final class RespawnKillableDynamicObject extends AbstractSimpleKillableDynamicObject implements RespawnDynamicObject {

    @Builder
    public RespawnKillableDynamicObject(long id,
                                        Coordinate coordinate,
                                        RealmMap realmMap,
                                        DynamicObjectSdb dynamicObjectSdb, String idName) {
        super(id, coordinate, realmMap, dynamicObjectSdb, idName);
    }


    @Override
    public int respawnTime() {
        return dynamicObjectSdb().getRegenInterval(idName()) * 10;
    }

    @Override
    public DynamicObjectType type() {
        return DynamicObjectType.KILLABLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RespawnKillableDynamicObject object)) return false;
        return Objects.equals(id(), object.id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id());
    }
}
