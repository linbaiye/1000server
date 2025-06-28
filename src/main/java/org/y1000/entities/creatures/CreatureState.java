package org.y1000.entities.creatures;

public interface CreatureState {

    int elapsedMillis();

    int totalMillis();

    void update(int delta);

}
