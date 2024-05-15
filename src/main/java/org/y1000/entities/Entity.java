package org.y1000.entities;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.message.AbstractInterpolation;
import org.y1000.message.serverevent.EntityEventListener;
import org.y1000.util.Coordinate;

public interface Entity {
    long id();

    Coordinate coordinate();

    void update(int delta);

    /**
     * Return true if we can be hit.
     * @return true/false.
     */
    default boolean attackable() {
        return true;
    }

    default void attackedBy(ViolentCreature attacker) {

    }

    AbstractInterpolation captureInterpolation();

    void registerOrderedEventListener(EntityEventListener listener);
}
