package org.y1000.entities;

import org.y1000.util.Coordinate;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

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

    private static final Map<Direction, Direction[]> NEIBOURS_MAP = new HashMap<>() {
        {
            put(Direction.UP, new Direction[]{Direction.UP_LEFT, Direction.UP_RIGHT});
            put(Direction.UP_LEFT, new Direction[]{Direction.UP, Direction.LEFT});
            put(Direction.UP_RIGHT, new Direction[]{Direction.UP, Direction.RIGHT});
            put(Direction.LEFT, new Direction[]{Direction.UP_LEFT, Direction.DOWN_LEFT});
            put(Direction.DOWN_LEFT, new Direction[]{Direction.LEFT, Direction.DOWN});
            put(Direction.DOWN, new Direction[]{Direction.DOWN_RIGHT, Direction.DOWN_LEFT});
            put(Direction.DOWN_RIGHT, new Direction[]{Direction.RIGHT, Direction.DOWN});
            put(Direction.RIGHT, new Direction[]{Direction.UP_RIGHT, Direction.DOWN_RIGHT});
        }
    };

    private static final Map<Direction, Direction> OPPOSITE = new HashMap<>() {
        {
            put(UP, DOWN);
            put(DOWN, UP);
            put(LEFT, RIGHT);
            put(RIGHT, LEFT);
            put(UP_LEFT, DOWN_RIGHT);
            put(UP_RIGHT, DOWN_LEFT);
            put(DOWN_RIGHT, UP_LEFT);
            put(DOWN_LEFT, UP_RIGHT);
        }
    };


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

    public Direction[] neighbours() {
        return NEIBOURS_MAP.get(this);
    }

    public Direction opposite() {
        return OPPOSITE.get(this);
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
