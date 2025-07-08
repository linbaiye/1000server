package org.y1000.entities.creatures.npc;

import org.y1000.message.NpcSnapshot;

public interface NpcAbility {

    boolean update(int delta);

    NpcSnapshot captureSnapshot(Npc npc);

}
