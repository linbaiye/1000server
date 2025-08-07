package org.y1000.event;

import org.y1000.entities.Entity;

public interface TypedEntityEvent<E extends Entity> {
    E source();
}
