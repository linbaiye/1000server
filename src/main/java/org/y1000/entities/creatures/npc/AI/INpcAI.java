package org.y1000.entities.creatures.npc.AI;


import org.y1000.entities.creatures.npc.INpc;
import org.y1000.entities.creatures.npc.NpcFrozenAI;

public interface INpcAI {

    void onActionDone(INpc npc);

    void onMoveFailed(INpc npc);

    void start(INpc npc);

    default void onDead(INpc npc) {
        npc.changeAndStartAI(NpcFrozenAI.INSTANCE);
    }
}
