package org.y1000.entities.creatures.npc.spell;

import org.y1000.entities.creatures.npc.INpc;

public interface NpcSpell {

    boolean canCast(INpc npc);

    default void reset() {

    }
}
