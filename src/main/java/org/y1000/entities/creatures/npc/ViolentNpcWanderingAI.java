package org.y1000.entities.creatures.npc;

public final class ViolentNpcWanderingAI extends AbstractWanderingNpcAI<ViolentNpc> {

    @Override
    protected void onHurtDone(ViolentNpc npc) {
        if (npc.state() instanceof NpcHurtState hurtState) {
            npc.changeAI(new ViolentNpcMeleeFightAI(hurtState.attacker(), npc));
        } else {
            throw new IllegalStateException();
        }
    }
}
