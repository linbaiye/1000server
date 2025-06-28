package org.y1000.event;



@Deprecated
public interface IEntityEvent extends EntityEvent {

    default void accept(EntityEventVisitor visitor) {

    }

}
