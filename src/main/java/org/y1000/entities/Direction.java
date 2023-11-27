package org.y1000.entities;

import org.y1000.message.MessageType;
import org.y1000.util.Coordinate;

public enum Direction {

    UP(new Coordinate(0, -1), 1),
    UP_RIGHT(new Coordinate(1, -1), 6),

    RIGHT(new Coordinate(1, 0), 3),

    DOWN_RIGHT(new Coordinate(1, 1), 4),

    DOWN(new Coordinate(0, 1), 0),

    DOWN_LEFT(new Coordinate(-1, 1), 5),

    LEFT(new Coordinate(-1, 0), 2),

    UP_LEFT(new Coordinate(-1, -1), 7),

    ;

    private final Coordinate offset;

    private final int value;

    Direction(Coordinate offset, int value) {
        this.offset = offset;
        this.value = value;
    }

    public Coordinate offset() {
        return offset;
    }

    public int value() {
        return value;
    }

    public static Direction fromValue(int v) {
        for (Direction value : values()) {
            if (value.value()  == v) {
                return value;
            }
        }
        return null;
    }

}
