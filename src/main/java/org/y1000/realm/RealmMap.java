package org.y1000.realm;


import org.y1000.entities.creatures.Creature;
import org.y1000.util.Coordinate;

import java.util.Optional;
import java.util.Set;

public interface RealmMap {

    boolean movable(Coordinate coordinate);

    void occupy(Creature creature);

    void free(Creature creature);

    static Optional<RealmMap> Load(String name) {
        return RealmMapV2Impl.read(name);
    }
}
