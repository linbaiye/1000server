package org.y1000.event;

import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.event.*;
import org.y1000.entities.creatures.event.INpcMoveEvent;
import org.y1000.message.*;
import org.y1000.message.serverevent.ShowItemEvent;

@Deprecated
public interface EntityEventVisitor {
    default void visit(TypedEntityEvent event) {

    }

    default void visit(AbstractPositionEvent positionEvent) {

    }


    default void visit(SetPositionEvent setPositionEvent) {
        visit((AbstractPositionEvent)setPositionEvent);
    }

    default void visit(NpcChangeStateEvent event) {
    }

    default void visit(CreatureDieEvent event) {
    }
    default void visit(EntitySoundEvent event) {
    }

    default void visit(RemoveEntityEvent event) {
    }
    default void visit(INpcMoveEvent event) {
    }

    default void visit(ShowItemEvent event) {
    }

}
