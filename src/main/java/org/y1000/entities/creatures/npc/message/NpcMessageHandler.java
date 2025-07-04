package org.y1000.entities.creatures.npc.message;

import org.y1000.entities.Entity;
import org.y1000.message.I2ClientMessage;

public interface NpcMessageHandler {
    void notifyVisiblePlayers(Entity entity, I2ClientMessage message);
}
