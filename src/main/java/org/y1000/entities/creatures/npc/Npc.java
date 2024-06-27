package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.Creature;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.Optional;
import java.util.Set;

public interface Npc extends Creature {

    void onActionDone();

    Rectangle wanderingArea();

    Coordinate spawnCoordinate();

    Optional<String> normalSound();

    void revive(Coordinate coordinate);
    void idle();
    void freeze();
    void move();
}
