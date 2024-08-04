package org.y1000.entities.creatures.npc;

import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.ViolentCreature;

import java.util.Optional;

public interface ViolentNpc extends ViolentCreature, Npc {

    void changeAI(NpcAI newAI);

    Optional<NpcRangedSkill> skill();

    Optional<String> attackSound();


    void startAttackAction(boolean withSound);

    void startRangedAttack(AttackableActiveEntity target);

}
