package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;
import org.y1000.util.Rectangle;

public interface Npc extends Creature {

    void onActionDone();

    void onMoveFailed();

    void move(int speed);

    Rectangle wanderingArea();

    Coordinate spawnCoordinate();

    void revive(Coordinate coordinate);

    void startAction(State state);

    void changeState(NpcState state);

    NpcState state();

    void start();
}
