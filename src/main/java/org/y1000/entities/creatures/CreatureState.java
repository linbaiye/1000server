package org.y1000.entities.creatures;


import org.y1000.message.clientevent.ClientEvent;
import org.y1000.message.clientevent.ClientInventoryEvent;

public interface CreatureState<C extends Creature> {
    State stateEnum();

    int elapsedMillis();

    void update(C c, int delta);

    default boolean attackable() {
        return true;
    }

    default void moveToHurtCoordinate(C creature) {

    }

    default boolean canHandle(ClientEvent event) {
        return true;
    }

    default State decideAfterHurtState() {
        return State.IDLE;
    }
}
