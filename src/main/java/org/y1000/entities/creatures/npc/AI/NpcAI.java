package org.y1000.entities.creatures.npc.AI;


import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcFrozenAI;

public interface NpcAI {

    void onActionDone(Npc npc);

    void onMoveFailed(Npc npc);

    void start(Npc npc);

    default void onDead(Npc npc) {
        npc.changeAndStartAI(NpcFrozenAI.INSTANCE);
    }

}
