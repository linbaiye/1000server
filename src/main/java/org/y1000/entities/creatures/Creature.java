package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;

public interface Creature extends Entity {

    Direction direction();

    void changeDirection(Direction newDirection);

    String name();
}
