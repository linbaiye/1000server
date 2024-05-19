package org.y1000.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.y1000.entities.Direction;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class CoordinateTest {

    @Getter
    @AllArgsConstructor
    private static class Testing {
        private Direction direction;

        public void change() {
            direction = Direction.DOWN;
        }
    }

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

    @Test
    void name() {
        System.out.println(50%900);
    }

    public static boolean isLeft(Point a, Point b, Point c) {
        return (b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x) < 0;
    }

    public static void main(String[] args) {
        Testing a = new Testing(Direction.LEFT);
        Direction v = a.direction;
        a.change();
        System.out.println(v);
        System.out.println(a.getDirection());
    }

}