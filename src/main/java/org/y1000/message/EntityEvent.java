package org.y1000.message;


import org.y1000.entities.Entity;

public interface EntityEvent extends ServerEvent {

    Entity source();

    default long id() {
        return source().id();
    }
}
