package org.y1000.util;

public record Rectangle(Coordinate start, Coordinate end) {

    public boolean contains(Coordinate coordinate) {
        return start.x() <= coordinate.x() && end.x() >= coordinate.x()
                && start.y() <= coordinate.y() && end.y() >= coordinate.y();
    }
}
