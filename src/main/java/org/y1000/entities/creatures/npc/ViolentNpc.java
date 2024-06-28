package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.ViolentCreature;

import java.util.Optional;

public interface ViolentNpc extends ViolentCreature, Npc {
    int runSpeed();

    void changeAI(NpcAI<ViolentNpc> newAI);

    Optional<NpcRangedSkill> rangedSkill();

}
