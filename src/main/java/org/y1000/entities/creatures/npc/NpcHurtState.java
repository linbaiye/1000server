package org.y1000.entities.creatures.npc;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.AbstractCreatureHurtState;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.creatures.monster.NpcStateEnum;

@Slf4j
public final class NpcHurtState implements NpcState {
//    /**
//     * The state before getting hurt.
//     */
//    private final NpcState previousState;
//
//    private final ViolentCreature attacker;
//
//    public NpcHurtState(int totalMillis, NpcState previousState, ViolentCreature attacker) {
//        super(totalMillis);
//        this.attacker = attacker;
//        if (previousState instanceof NpcHurtState hurtState)
//            this.previousState = hurtState.previousState;
//        else
//            this.previousState = previousState;
//    }
//
//    public ViolentCreature attacker() {
//        return attacker;
//    }
//
//    protected void recovery(Npc npc) {
//        npc.onActionDone();
//    }
//
//    public NpcState previousState() {
//        return previousState;
//    }
//
//    @Override
//    public void afterHurt(Npc npc) {
//        recovery(npc);
//    }
//
//    @Override
//    public NpcStateEnum stateEnum() {
//        return null;
//    }
//
//    @Override
//    public boolean attackable() {
//        return false;
//    }
//
//    @Override
//    public void moveToHurtCoordinate(Npc creature) {
//        super.moveToHurtCoordinate(creature);
//    }
//
//    @Override
//    public NpcStateEnum state() {
//        return NpcStateEnum.Hurt;
//    }


    @Override
    public int elapsedMillis() {
        return 0;
    }

    @Override
    public int totalMillis() {
        return 0;
    }

    @Override
    public void update(int delta) {

    }

    @Override
    public NpcStateEnum stateEnum() {
        return NpcStateEnum.Hurt;
    }
}
