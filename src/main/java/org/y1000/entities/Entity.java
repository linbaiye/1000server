package org.y1000.entities;

import org.y1000.message.AbstractCreatureInterpolation;
import org.y1000.util.Coordinate;

/**
 * An entity is something that can affect or be visible to all insight players.
 */
public interface Entity {

    long id();

    Coordinate coordinate();

    void update(int delta);

    AbstractCreatureInterpolation captureInterpolation();
}
