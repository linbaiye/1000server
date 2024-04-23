package org.y1000.entities.managers;

import org.y1000.entities.Entity;
import org.y1000.util.Coordinate;

public interface EntityManager<T extends Entity> {

    void update(long delta);
}
