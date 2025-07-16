package org.y1000.entities;

import org.y1000.realm.RealmMap;
import org.y1000.util.Coordinate;

public interface AttackableEntity extends org.y1000.entities.ActiveEntity {
    /**
     * Return true if we can be hit.
     * @return true/false.
     */
    boolean canBeAttackedNow();

    RealmMap realmMap();

    default boolean isWithinMeleeRange(Coordinate coordinate) {
        return coordinate != null && coordinate().directDistance(coordinate) < 2;
    }
}
