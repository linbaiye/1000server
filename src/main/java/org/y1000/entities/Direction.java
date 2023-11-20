package org.y1000.entities;

import org.y1000.util.Coordinate;

public enum Direction {

    UP(new Coordinate(0, -1)),
    UP_RIGHT(new Coordinate(1, -1)),

    RIGHT(new Coordinate(1, 0)),

    DOWN_RIGHT(new Coordinate(1, 1)),

    DOWN(new Coordinate(0, 1)),

    DOWN_LEFT(new Coordinate(-1, 1)),

    LEFT(new Coordinate(-1, 0)),

    UP_LEFT(new Coordinate(-1, -1)),

    ;

    private final Coordinate offset;

    Direction(Coordinate offset) {
        this.offset = offset;
    }

    public Coordinate offset()
    {
        return offset;
    }
}
