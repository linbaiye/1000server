package org.y1000.entities.creatures.npc;


import org.y1000.entities.creatures.monster.NpcActionEnum;

public interface NpcAction {

    boolean update(int delta);

    int elapsedMillis();

    NpcActionEnum actionEnum();

}
