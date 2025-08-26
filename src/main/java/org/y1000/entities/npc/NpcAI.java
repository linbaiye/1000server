package org.y1000.entities.npc;

import org.y1000.entities.npc.event.NpcSnapshot;

public interface NpcAI {

    void update(int delta);

    NpcSnapshot captureSnapshot();

    void start();

    void instantKill();



}
