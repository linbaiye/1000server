package org.y1000.entities.creatures.npc;


import org.y1000.entities.creatures.CreatureState;
import org.y1000.entities.creatures.monster.NpcActionEnum;

public interface NpcState extends CreatureState {
    /**
     * What to do after hurt.
     * @param npc the npc got hurt.
     */
    default void afterHurt(INpc npc) {
        npc.onActionDone();
    }

    NpcActionEnum stateEnum();
}
