package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

public final class AiPathUtil {
    public static Direction computeNextMoveDirection(Creature creature,
                                                 Coordinate dest, Coordinate previous) {
        int minDist = Integer.MAX_VALUE;
        Direction towards = null;
        for (Direction direction : Direction.values()) {
            Coordinate coordinate = creature.coordinate().moveBy(direction);
            int distance = coordinate.distance(dest);
            if (creature.realmMap().movable(coordinate) && !previous.equals(coordinate) && minDist > distance) {
                minDist = distance;
                towards = direction;
            }
        }
        return towards;
    }
}
