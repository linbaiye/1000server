package org.y1000.event.item;

import org.y1000.event.EntityEventVisitor;
import org.y1000.message.PlayerDropItemEvent;

public interface ItemEventVisitor extends EntityEventVisitor {
    default void visit(PlayerDropItemEvent dropItemEvent) {

    }
}
