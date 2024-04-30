package org.y1000.entities;

import org.y1000.message.AbstractInterpolation;
import org.y1000.message.serverevent.EntityEventListener;
import org.y1000.util.Coordinate;

public interface Entity {
    long id();

    Coordinate coordinate();

    void update(int delta);

    default void hit(Entity attacker) {

    }

    AbstractInterpolation captureInterpolation();

    void registerOrderedEventListener(EntityEventListener listener);
}
