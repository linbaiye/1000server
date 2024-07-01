package org.y1000.entities.creatures.npc;

public final class ViolentNpcWanderingAI extends AbstractWanderingNpcAI {

    @Override
    protected void onHurtDone(Npc npc) {
        ViolentNpc violentNpc = (ViolentNpc) npc;
        if (npc.state() instanceof NpcHurtState hurtState) {
            violentNpc.changeAI(new ViolentNpcMeleeFightAI(hurtState.attacker(), violentNpc));
        } else {
            throw new IllegalStateException();
        }
    }
}
