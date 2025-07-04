package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.npc.event.NpcEvent;

public interface NpcEventListener {
    void onEvent(NpcEvent npcEvent);
}
