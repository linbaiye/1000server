package org.y1000.entities.npc;

import org.y1000.entities.npc.event.NpcEvent;

public interface NpcEventListener {
    void onEvent(NpcEvent npcEvent);
}
