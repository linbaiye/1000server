package org.y1000.entities.creatures.npc;


import org.y1000.entities.creatures.monster.NpcAnimationEnum;

public interface NpcAction {

    boolean update(int delta);

    int elapsedMillis();

    NpcAnimationEnum actionEnum();

}
