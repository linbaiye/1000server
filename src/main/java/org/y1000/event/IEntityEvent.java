package org.y1000.event;


import org.y1000.entities.Entity;

@Deprecated
public interface IEntityEvent extends EntityEvent<Entity> {

    default void accept(EntityEventVisitor visitor) {

    }

}
