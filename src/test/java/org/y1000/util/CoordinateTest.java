package org.y1000.util;

import org.y1000.entities.Direction;

import static org.junit.jupiter.api.Assertions.*;

class CoordinateTest {

    @org.junit.jupiter.api.Test
    void move() {
        var origin = new Coordinate(0, 0);
        assertEquals(new Coordinate(0, -1), origin.moveBy(Direction.UP));
        assertEquals(new Coordinate(1, -1), origin.moveBy(Direction.UP_RIGHT));
        assertEquals(new Coordinate(1, 0), origin.moveBy(Direction.RIGHT));
        assertEquals(new Coordinate(1, 1), origin.moveBy(Direction.DOWN_RIGHT));
        assertEquals(new Coordinate(0, 1), origin.moveBy(Direction.DOWN));
        assertEquals(new Coordinate(-1, 1), origin.moveBy(Direction.DOWN_LEFT));
        assertEquals(new Coordinate(-1, 0), origin.moveBy(Direction.LEFT));
        assertEquals(new Coordinate(-1, -1), origin.moveBy(Direction.UP_LEFT));
    }

    @org.junit.jupiter.api.Test
    void add() {
        assertEquals(new Coordinate(2, 2), new Coordinate(1, 1).add(new Coordinate(1, 1)));
    }
}