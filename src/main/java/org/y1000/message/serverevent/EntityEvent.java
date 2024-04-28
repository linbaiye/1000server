package org.y1000.message.serverevent;


import org.y1000.entities.Entity;

public interface EntityEvent {

    Entity source();

    default long id() {
        return source().id();
    }

    void accept(EntityEventHandler visitor);
}
