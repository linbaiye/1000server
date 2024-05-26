package org.y1000.entities.players;

import org.y1000.message.serverevent.EntityEvent;

public interface EventEmiter {
    void emitEvent(EntityEvent event);
}
