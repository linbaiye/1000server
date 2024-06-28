package org.y1000.entities.creatures.npc;


public interface NpcAI<N extends Npc> {

    void onActionDone(N npc);

    void onMoveFailed(N npc);

    void start(N npc);

}
