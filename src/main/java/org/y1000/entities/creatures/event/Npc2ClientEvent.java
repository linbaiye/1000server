package org.y1000.entities.creatures.event;

import org.y1000.event.EntityEvent;
import org.y1000.message.ServerMessage;
import org.y1000.message.serverevent.Visibility;

public interface Npc2ClientEvent extends ServerMessage, EntityEvent {
    Visibility getVisibility();
}
