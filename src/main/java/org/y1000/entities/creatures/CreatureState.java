package org.y1000.entities.creatures;

import org.y1000.entities.Entity;
import org.y1000.entities.players.State;

public interface CreatureState<E extends Creature> {
    State stateEnum();

    long elapsedMillis();

    void update(E e, int delta);

    default void attack(Entity target) {

    }
}
