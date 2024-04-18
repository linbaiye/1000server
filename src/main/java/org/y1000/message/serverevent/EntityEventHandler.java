package org.y1000.message.serverevent;

import org.y1000.message.*;

public interface EntityEventHandler {
    default void handle(EntityEvent event) {

    }

    default void handle(AbstractPositionEvent positionEvent) {

    }

    default void handle(MoveEvent moveEvent) {
        handle((AbstractPositionEvent)moveEvent);
    }

    default void handle(SetPositionEvent setPositionEvent) {
        handle((AbstractPositionEvent)setPositionEvent);
    }
}
