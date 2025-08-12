package org.y1000.util;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.Direction;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public record Coordinate(int x, int y) {

    public static final Coordinate Empty = new Coordinate(0, 0);
    public static final int VISIBLE_X_RANGE = 15;
    public static final int VISIBLE_Y_RANGE = 13;

    public Coordinate moveBy(Direction direction) {
        Validate.notNull(direction);
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


    public Direction directionTo(Coordinate to) {
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

    public boolean isWithinVisibleRange(Coordinate another) {
        return another != null &&
                another.x() >= x() - Coordinate.VISIBLE_X_RANGE &&
                another.x() <= x() + Coordinate.VISIBLE_X_RANGE &&
                another.y() >= y() - Coordinate.VISIBLE_Y_RANGE &&
                another.y() <= y() + Coordinate.VISIBLE_Y_RANGE;
    }

    public Direction directionByAngle(Coordinate end) {
        var angle = (float) Math.atan2(end.y() - y, end.x() - x);
        var tmp = snapped(angle,  (float) (Math.PI / 4)) / (Math.PI / 4);
        int dir = wrap((int) tmp , 0, 8);
        return switch(dir) {
            case 0 -> Direction.RIGHT;
            case 1 -> Direction.DOWN_RIGHT;
            case 2 -> Direction.DOWN;
            case 3 ->Direction.DOWN_LEFT;
            case 4 ->Direction.LEFT;
            case 5 ->Direction.UP_LEFT;
            case 6 ->Direction.UP;
            default-> Direction.UP_RIGHT;
        } ;
    }


    private int wrap(int value, int min, int max)
    {
        int num = max - min;
        return num == 0 ? min : min + ((value - min) % num + num) % num;
    }

    private float snapped(float s, float step)
    {
        return step != 0.0 ? (float) (Math.floor((float) ((double) s / (double) step + 0.5)) * step) : s;
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


    public Direction bestDirectionTo(Coordinate dest, Predicate<Coordinate> movable) {
        Direction towards = null;
        var dir = this.directionTo(dest);
        if (movable.test(this.moveBy(dir))) {
            return dir;
        }
        int minDist = Integer.MAX_VALUE;
        for (Direction direction : Direction.values()) {
            var next = this.moveBy(direction);
            if (!movable.test(next)) {
                continue;
            }
            int distance = next.distance(dest);
            if (minDist > distance) {
                minDist = distance;
                towards = direction;
            }
        }
        return towards;
    }

}
