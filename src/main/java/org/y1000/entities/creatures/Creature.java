package org.y1000.entities.creatures;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.players.State;

public interface Creature extends Entity {

    Direction direction();

    State stateEnum();

    void changeDirection(Direction newDirection);

    String name();
}
