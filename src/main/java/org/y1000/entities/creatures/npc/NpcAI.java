package org.y1000.entities.creatures.npc;

public interface NpcAI {

    default void onAttacked(AttackAction action) {

    }

    void update(int delta);

    NpcAction currentAction();

}
