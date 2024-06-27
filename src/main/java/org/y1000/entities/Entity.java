package org.y1000.entities;

import org.y1000.event.EntityEvent;
import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.event.EntityEventListener;
import org.y1000.util.Coordinate;

/**
 * An entity is something that can affect or be visible to all insight players.
 */
public interface Entity {
    int VISIBLE_X_RANGE = 15;

    int VISIBLE_Y_RANGE = 15;

    long id();

    Coordinate coordinate();


    default boolean canBeSeenAt(Coordinate another) {
        return another.x() >= coordinate().x() - VISIBLE_X_RANGE &&
                another.x() <= coordinate().x() + VISIBLE_X_RANGE &&
                another.y() >= coordinate().y() - VISIBLE_Y_RANGE &&
                another.y() <= coordinate().y() + VISIBLE_Y_RANGE;
    }

    void update(int delta);

    AbstractEntityInterpolation captureInterpolation();

    void emitEvent(EntityEvent event);

    void registerEventListener(EntityEventListener listener);

    void deregisterEventListener(EntityEventListener listener);
}
