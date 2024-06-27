package org.y1000.entities.creatures.npc;


public interface NpcAI {
    void onHurtDone(Npc npc);

    void onIdleDone(Npc npc);

    void onMoveDone(Npc npc);

    void onFrozenDone(Npc npc);

    void start(Npc npc);

}
