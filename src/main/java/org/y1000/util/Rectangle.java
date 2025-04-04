package org.y1000.util;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.npc.WanderArea;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

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
        var x = ThreadLocalRandom.current().nextInt(minX, maxX + 1);
        int minY = Math.max(origin.y() - 3, start.y());
        int maxY = Math.min(origin.y() + 3, end.y());
        var y = ThreadLocalRandom.current().nextInt(minY, maxY + 1);
        return new Coordinate(x, y);
    }

    public Coordinate randomOutSpawnScope(Coordinate coordinate) {
        var xdist = Math.max((end.x() - start.x()) / 2, 3);
        var ydist = Math.max((end.y() - start.y()) / 2, 3);
        var x = ThreadLocalRandom.current().nextInt(coordinate.x() - xdist, coordinate.x() + xdist);
        var y = ThreadLocalRandom.current().nextInt(coordinate.y() - ydist, coordinate.y() + ydist);
        return new Coordinate(x, y);
    }


    public Optional<Coordinate> random(Predicate<Coordinate> checker) {
        Coordinate random = random();
        return checker.test(random) ? Optional.of(random) : Optional.empty();
    }


    public Optional<Coordinate> findFirst(Predicate<Coordinate> checker) {
        for (int i = start.x(); i <= end.x(); i++) {
            for (int j = start.y(); j <= end.y(); j++) {
                Coordinate coor = Coordinate.xy(i, j);
                if (checker.test(coor)) {
                    return Optional.of(coor);
                }
            }
        }
        return Optional.empty();
    }


    private Coordinate random() {
        int x;
        if (start.x() == end.x()) {
            x = start.x();
        } else {
            x = ThreadLocalRandom.current().nextInt(start().x(), end.x());
        }
        int y;
        if (start.y() == end.y()) {
            y = start.y();
        } else {
            y = ThreadLocalRandom.current().nextInt(start().y(), end.y());
        }
        return new Coordinate(x, y);
    }
}
