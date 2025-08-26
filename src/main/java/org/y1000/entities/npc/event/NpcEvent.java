package org.y1000.entities.npc.event;

import org.y1000.entities.npc.Npc;
import org.y1000.entities.TypedEntityEvent;
import org.y1000.realm.NpcEventHandler;

public interface NpcEvent extends TypedEntityEvent<Npc> {
    void accept(NpcEventHandler handler);

}
