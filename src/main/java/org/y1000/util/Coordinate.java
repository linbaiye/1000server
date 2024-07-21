package org.y1000.util;

import org.y1000.entities.Direction;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record Coordinate(int x, int y) {

    public static final Coordinate Empty = new Coordinate(0, 0);

    public Coordinate moveBy(Direction direction) {
        return add(direction.offset());
    }

    private Coordinate add(Coordinate coordinate) {
        return new Coordinate(x + coordinate.x, y + coordinate.y);
    }

    public Set<Coordinate> neighbours() {
        Set<Coordinate> result = new HashSet<>(Direction.values().length);
        for (Direction value : Direction.values()) {
            result.add(moveBy(value));
        }
        return result;
    }


    public Direction computeDirection(Coordinate to) {
        var ydiff = to.y() - this.y();
        var xdiff = to.x() - this.x();
        if (ydiff < 0) {
            return xdiff < 0 ? Direction.UP_LEFT :
                    xdiff > 0 ? Direction.UP_RIGHT : Direction.UP;
        } else if (ydiff == 0) {
            return xdiff > 0 ? Direction.RIGHT: Direction.LEFT;
        } else {
            return xdiff < 0 ? Direction.DOWN_LEFT:
                    xdiff > 0 ? Direction.DOWN_RIGHT: Direction.DOWN;
        }
    }

    public int directDistance(Coordinate another) {
        return Math.max(Math.abs(another.x() - this.x()), Math.abs(another.y() - this.y()));
    }

    public int distance(Coordinate another) {
        return (another.x() - x) * (another.x() - x) + (another.y() - y) * (another.y() - y);
    }

    public int xDistance(int x) {
        return Math.abs(x() - x);
    }

    public int yDistance(int y) {
        return Math.abs(y() - y);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public Coordinate move(int x, int y) {
        return new Coordinate(x() + x, y() + y);
    }

    public static Coordinate xy(int x, int y) {
        return new Coordinate(x, y);
    }

}
