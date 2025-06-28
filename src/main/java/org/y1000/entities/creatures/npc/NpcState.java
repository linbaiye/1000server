package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.ICreatureState;

public interface NpcState extends ICreatureState<Npc> {
    /**
     * What to do after hurt.
     * @param npc the npc got hurt.
     */
    default void afterHurt(Npc npc) {
        npc.onActionDone();
    }
}
