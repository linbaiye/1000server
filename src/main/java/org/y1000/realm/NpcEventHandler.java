package org.y1000.realm;


import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.event.FilterVisibleEntityEvent;
import org.y1000.message.I2ClientMessage;

public interface NpcEventHandler extends EntityEventHandler {

    void onMoved(Npc npc, I2ClientMessage message);

    void onRemove(Npc npc, I2ClientMessage message);

    void filter(FilterVisibleEntityEvent event);
}
