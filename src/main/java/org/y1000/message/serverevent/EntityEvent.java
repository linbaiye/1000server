package org.y1000.message.serverevent;


import org.y1000.entities.Entity;
import org.y1000.message.ServerMessage;

public interface EntityEvent extends ServerMessage {

    Entity source();

    default long id() {
        return source().id();
    }

    void accept(EntityEventHandler visitor);
}
