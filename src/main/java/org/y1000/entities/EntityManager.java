package org.y1000.entities;

import org.y1000.util.Coordinate;

import java.util.Set;

public interface EntityManager<T extends Entity> {

    T findOne(Coordinate coordinate);
}
