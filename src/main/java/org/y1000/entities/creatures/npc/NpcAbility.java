package org.y1000.entities.creatures.npc;

public interface NpcAbility {
    boolean update(int delta);

    int apply(Npc npc);

}
