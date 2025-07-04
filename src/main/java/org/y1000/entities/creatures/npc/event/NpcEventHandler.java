package org.y1000.entities.creatures.npc.event;


import org.y1000.entities.Entity;
import org.y1000.message.I2ClientMessage;

public interface NpcEventHandler {

    void sendToVisiblePlayers(Entity entity, I2ClientMessage message);

    void updateAOIAndNotifyPlayers(Entity npc);

}
