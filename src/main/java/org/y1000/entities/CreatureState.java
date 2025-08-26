package org.y1000.entities;

public interface CreatureState {

    int elapsedMillis();

    int totalMillis();

    void update(int delta);

}
