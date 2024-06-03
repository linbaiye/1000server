package org.y1000.message.serverevent;

import org.y1000.entities.creatures.event.CreatureChangeStateEvent;
import org.y1000.entities.creatures.event.CreatureAttackEvent;
import org.y1000.entities.creatures.event.CreatureHurtEvent;
import org.y1000.entities.creatures.event.CreatureShootEvent;
import org.y1000.entities.players.event.RewindEvent;
import org.y1000.message.*;

public interface EntityEventVisitor {
    default void visit(EntityEvent event) {

    }

    default void visit(AbstractPositionEvent positionEvent) {

    }

    default void visit(CreatureAttackEvent event) {
        visit((EntityEvent)event);
    }

    default void visit(CreatureHurtEvent hurtEvent) {
        visit((EntityEvent)hurtEvent);
    }

    default void visit(MoveEvent moveEvent) {
        visit((AbstractPositionEvent)moveEvent);
    }

    default void visit(SetPositionEvent setPositionEvent) {
        visit((AbstractPositionEvent)setPositionEvent);
    }

    default void visit(CreatureChangeStateEvent event) {
        visit((EntityEvent) event);
    }

    default void visit(RewindEvent event) {
        visit((EntityEvent) event);
    }

    default void visit(CreatureShootEvent event) {
        visit((EntityEvent) event);
    }

}
