package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.entities.PhysicalEntity;
import org.y1000.util.Coordinate;

public interface Creature extends PhysicalEntity {

    Direction direction();

    State stateEnum();

    void changeDirection(Direction newDirection);

    void changeCoordinate(Coordinate coordinate);

    int avoidance();

    String name();
}
