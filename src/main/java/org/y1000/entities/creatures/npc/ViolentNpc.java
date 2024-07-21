package org.y1000.entities.creatures.npc;

import org.y1000.entities.AttackableEntity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.ViolentCreature;

import java.util.Optional;

public interface ViolentNpc extends ViolentCreature, Npc {

    void changeAI(NpcAI newAI);

    Optional<NpcRangedSkill> skill();

    Optional<String> attackSound();

    default int walkSpeedInFight() {
        int stateMillis = getStateMillis(State.WALK);
        return skill().isEmpty() ? stateMillis : (int)(stateMillis / 1.7);
    }

    void startAttackAction(boolean withSound);

    void startRangedAttack(AttackableEntity target);

}
