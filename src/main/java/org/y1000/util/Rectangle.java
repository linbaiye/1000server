package org.y1000.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public record Rectangle(Coordinate start, Coordinate end) {

    public boolean contains(Coordinate coordinate) {
        return start.x() <= coordinate.x() && end.x() >= coordinate.x()
                && start.y() <= coordinate.y() && end.y() >= coordinate.y();
    }

    public Coordinate random(Coordinate origin) {
        if (!contains(origin)) {
            return origin;
        }
        int minX = Math.max(origin.x() - 3, start.x());
        int maxX = Math.min(origin.x() + 3, end.x());
        var x = ThreadLocalRandom.current().nextInt(minX, maxX);
        int minY = Math.max(origin.y() - 3, start.y());
        int maxY = Math.min(origin.y() + 3, end.y());
        var y = ThreadLocalRandom.current().nextInt(minY, maxY);
        return new Coordinate(x, y);
    }
}
