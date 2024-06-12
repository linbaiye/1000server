package org.y1000.realm;


import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.Creature;
import org.y1000.util.Coordinate;

import java.util.Optional;

public interface RealmMap {

    boolean movable(Coordinate coordinate);

    void occupy(PhysicalEntity creature);

    void free(PhysicalEntity creature);

    String name();

    static Optional<RealmMap> Load(String name) {
        return RealmMapV2Impl.read(name);
    }
}
