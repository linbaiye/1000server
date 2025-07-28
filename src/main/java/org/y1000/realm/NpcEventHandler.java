package org.y1000.realm;


import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.event.NpcShootEvent;
import org.y1000.message.I2ClientMessage;
import org.y1000.util.Coordinate;

public interface NpcEventHandler extends EntityEventHandler {

    void onMoved(Npc npc, I2ClientMessage message);

    void onRemove(Npc npc, I2ClientMessage message);

    void shoot(NpcShootEvent event);

    void dropItem(String name, int number, Coordinate dropAt);

    void copy(Npc npc, int number, ActiveEntity enemy);

    void onDie(Npc npc, I2ClientMessage message);
}
