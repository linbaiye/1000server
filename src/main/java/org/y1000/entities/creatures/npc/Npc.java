package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

import java.util.Optional;

public interface Npc extends Creature {

    void onActionDone();

    void onMoveFailed();

    void move(int speed);

    Rectangle wanderingArea();

    Coordinate spawnCoordinate();

    Optional<String> normalSound();

    void revive(Coordinate coordinate);

    void startAction(State state);

    void changeAction(NpcState state);

    NpcState state();

    void start();
}
