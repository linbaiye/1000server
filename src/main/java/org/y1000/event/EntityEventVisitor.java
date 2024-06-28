package org.y1000.event;

import org.y1000.entities.RemoveEntityEvent;
import org.y1000.entities.creatures.event.*;
import org.y1000.message.*;
import org.y1000.message.serverevent.ShowItemEvent;

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

    default void visit(SetPositionEvent setPositionEvent) {
        visit((AbstractPositionEvent)setPositionEvent);
    }

    default void visit(NpcChangeStateEvent event) {
        visit((EntityEvent) event);
    }

    default void visit(CreatureDieEvent event) {
        visit((EntityEvent) event);
    }
    default void visit(EntitySoundEvent event) {
        visit((EntityEvent) event);
    }

    default void visit(RemoveEntityEvent event) {
        visit((EntityEvent) event);
    }
    default void visit(MonsterShootEvent event) {
        visit((EntityEvent) event);
    }

    default void visit(NpcMoveEvent event) {
        visit((EntityEvent) event);
    }

    default void visit(ShowItemEvent event) {
        visit((EntityEvent) event);
    }

    default void visit(NpcJoinedEvent event) {
        visit((EntityEvent) event);
    }
}
