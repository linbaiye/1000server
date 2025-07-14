package org.y1000.entities.creatures.npc;

import org.y1000.entities.AttackableEntity;
import org.y1000.entities.creatures.IActiveEntity;

import java.util.Optional;

public interface ViolentNpc extends IActiveEntity, INpc {

    Optional<NpcRangedSkill> skill();

    Optional<String> attackSound();


    void startAttackAction(boolean withSound);

    void startRangedAttack(AttackableEntity target);

}
