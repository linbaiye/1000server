package org.y1000.realm;

import org.y1000.util.Coordinate;

public class V2Map implements RealmMap {
    @Override
    public boolean movable(Coordinate coordinate) {
        return false;
    }
}
