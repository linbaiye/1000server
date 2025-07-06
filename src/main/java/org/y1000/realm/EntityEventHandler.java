package org.y1000.realm;

import org.y1000.entities.Entity;
import org.y1000.message.I2ClientMessage;

public interface EntityEventHandler {

    void sendToVisiblePlayers(Entity source, I2ClientMessage message);
}
