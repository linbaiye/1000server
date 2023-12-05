package org.y1000.util;

import org.y1000.entities.Direction;

import java.util.HashSet;
import java.util.Set;

public record Coordinate(int x, int y) {

    public Coordinate moveBy(Direction direction) {
        return add(direction.offset());
    }

    public Coordinate add(Coordinate coordinate) {
        return new Coordinate(x + coordinate.x, y + coordinate.y);
    }

    public Set<Coordinate> neibours() {
        Set<Coordinate> result = new HashSet<>(Direction.values().length);
        for (Direction value : Direction.values()) {
            result.add(moveBy(value));
        }
        return result;
    }

    public static Coordinate xy(int x, int y) {
        return new Coordinate(x, y);
    }

}
