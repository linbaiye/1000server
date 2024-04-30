package org.y1000.entities.managers;

import org.y1000.entities.Entity;

public interface EntityManager<T extends Entity> {

    void update(int delta);
}
