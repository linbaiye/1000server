package org.y1000.entities.creatures.npc;



public interface NpcAI {

    void onActionDone(Npc npc);

    void onMoveFailed(Npc npc);

    void start(Npc npc);

    NpcAI DONOTHING = NpcFrozenAI.INSTANCE;

}
