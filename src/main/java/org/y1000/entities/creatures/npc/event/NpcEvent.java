package org.y1000.entities.creatures.npc.event;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.TypedEntityEvent;
import org.y1000.realm.NpcEventHandler;

public interface NpcEvent extends TypedEntityEvent<Npc> {
    void accept(NpcEventHandler handler);

}
