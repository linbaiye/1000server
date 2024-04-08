package org.y1000.entities;

import org.y1000.util.Coordinate;

import java.util.Set;

public interface GroundEntity extends Entity {
    Set<Coordinate> coordinates();
}
