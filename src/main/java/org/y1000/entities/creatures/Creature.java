package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.util.Coordinate;

public interface Creature extends Entity {

    Direction direction();

    State stateEnum();

    void changeDirection(Direction newDirection);

    void changeCoordinate(Coordinate coordinate);

    default int avoidance() {
        return 25;
    }

    String name();

    default int armor() {
        return 0;
    }
}
