package org.y1000.event;

import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.event.*;
import org.y1000.message.*;

@Deprecated
public interface EntityEventVisitor {
    default void visit(TypedEntityEvent event) {

    }

    default void visit(AbstractPositionEvent positionEvent) {

    }


    default void visit(EntitySoundEvent event) {
    }


}
