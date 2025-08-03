package org.y1000.entities.creatures.npc;

import org.y1000.message.NpcSnapshot;

public interface NpcAI {

    void update(int delta);

    NpcSnapshot captureSnapshot();

    void start();

    void instantKill();



}
