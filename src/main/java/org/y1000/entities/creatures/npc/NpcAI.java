package org.y1000.entities.creatures.npc;

public interface NpcAI {

    void update(int delta);

    NpcAction currentAction();

}
