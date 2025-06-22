package org.y1000.entities.creatures.event;

import org.y1000.event.EntityEvent;
import org.y1000.message.I2ClientMessage;
import org.y1000.message.serverevent.Visibility;

public interface Npc2ClientEvent extends I2ClientMessage, EntityEvent {
    Visibility getVisibility();
}
