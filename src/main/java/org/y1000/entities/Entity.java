package org.y1000.entities;

import org.y1000.message.AbstractInterpolation;
import org.y1000.util.Coordinate;

import java.util.Set;

public interface Entity {
    long id();

    Coordinate coordinate();

    default Set<Coordinate> coordinates() {
        return Set.of(coordinate());
    }

    void update(long delta);

    AbstractInterpolation captureInterpolation();
}
