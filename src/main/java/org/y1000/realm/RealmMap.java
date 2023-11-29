package org.y1000.realm;


import org.y1000.util.Coordinate;

import java.util.Optional;

public interface RealmMap {

    boolean movable(Coordinate coordinate);

    static Optional<RealmMap> Load(String name) {
        return RealmMapV2Impl.read(name);
    }
}
