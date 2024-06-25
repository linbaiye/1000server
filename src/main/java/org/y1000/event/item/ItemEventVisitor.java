package org.y1000.event.item;

import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventVisitor;
import org.y1000.message.PlayerDropItemEvent;
import org.y1000.message.serverevent.ShowItemEvent;

public interface ItemEventVisitor extends EntityEventVisitor {
    default void visit(ShowItemEvent event) {
        visit((EntityEvent) event);
    }
    default void visit(PlayerDropItemEvent dropItemEvent) {

    }
}
