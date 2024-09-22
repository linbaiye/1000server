package org.y1000.entities.creatures.npc;

import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.creatures.ViolentCreature;

import java.util.Optional;

public interface ViolentNpc extends ViolentCreature, Npc {

    void changeAndStartAI(NpcAI newAI);

    void changeAI(NpcAI newAI);

    void changeToIdleAI();

    void startIdleAI();

    Optional<NpcRangedSkill> skill();

    Optional<String> attackSound();


    void startAttackAction(boolean withSound);

    void startRangedAttack(AttackableActiveEntity target);

}
