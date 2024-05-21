package org.y1000.message.serverevent;


import org.y1000.entities.PhysicalEntity;

public interface EntityEvent {

    PhysicalEntity source();

    void accept(EntityEventVisitor visitor);
}
