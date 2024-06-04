package org.y1000.entities.creatures;


public interface CreatureState<C extends Creature> {
    State stateEnum();

    int elapsedMillis();

    int totalMillis();

    void update(C c, int delta);

    default boolean attackable() {
        return true;
    }

    default void moveToHurtCoordinate(C creature) {

    }

    default State decideAfterHurtState() {
        return State.IDLE;
    }
}
