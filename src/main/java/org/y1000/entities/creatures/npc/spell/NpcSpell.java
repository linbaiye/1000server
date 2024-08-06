package org.y1000.entities.creatures.npc.spell;

import org.y1000.entities.creatures.npc.Npc;

public interface NpcSpell {

    boolean canCast(Npc npc);

    default void reset() {

    }
}
