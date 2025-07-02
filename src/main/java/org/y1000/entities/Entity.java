package org.y1000.entities;

import org.y1000.message.I2ClientMessage;
import org.y1000.util.Coordinate;

/**
 * An entity is something that can affect or be visible to all insight players.
 */
public interface Entity {

    long id();

    Coordinate coordinate();

    default boolean canBeSeenAt(Coordinate another) {
        return another != null && another.isWithinVisibleRange(coordinate());
    }

    I2ClientMessage captureSnapshot();
}
