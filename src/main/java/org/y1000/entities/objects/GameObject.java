package org.y1000.entities.objects;

import org.y1000.util.Coordinate;

import java.util.Set;

public interface GameObject {
    Set<Coordinate> occupyingCoordinates();
}
