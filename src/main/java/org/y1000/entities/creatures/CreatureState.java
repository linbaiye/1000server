package org.y1000.entities.creatures;

public interface CreatureState<C extends Creature> {
    State stateEnum();

    long elapsedMillis();

    void update(C c, int delta);

    default void getAttacked(C c, Creature attacker) {

    }

}
