package org.y1000.event;


import org.y1000.entities.Entity;

public interface EntityEvent {

    Entity source();

    void accept(EntityEventVisitor visitor);
}
