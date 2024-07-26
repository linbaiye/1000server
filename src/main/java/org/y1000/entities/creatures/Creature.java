package org.y1000.entities.creatures;

import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

import java.util.Optional;

public interface Creature extends AttackableActiveEntity {

    Direction direction();

    State stateEnum();

    void changeDirection(Direction newDirection);

    void changeCoordinate(Coordinate coordinate);

    int avoidance();

    String viewName();

    int maxLife();

    int currentLife();

    int bodyArmor();

    Optional<String> hurtSound();

    Optional<String> dieSound();

    int getStateMillis(State state);

}
