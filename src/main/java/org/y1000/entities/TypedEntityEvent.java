package org.y1000.entities;


public interface TypedEntityEvent<E extends Entity> {
    E source();
}
