package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;

import java.util.Optional;

public interface ViolentNpc extends ViolentCreature, Npc {
    int runSpeed();

    void changeAI(NpcAI newAI);

    Optional<NpcRangedSkill> rangedSkill();

    Optional<String> attackSound();

    void startAttackAction(boolean withSound);


}
