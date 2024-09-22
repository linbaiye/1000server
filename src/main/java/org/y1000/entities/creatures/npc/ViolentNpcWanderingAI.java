package org.y1000.entities.creatures.npc;

import org.y1000.util.Coordinate;

public final class ViolentNpcWanderingAI extends AbstractWanderingNpcAI {

    public ViolentNpcWanderingAI(Coordinate dest) {
        super(dest, Coordinate.Empty);
    }

    @Override
    protected void onHurtDone(Npc npc) {
        ViolentNpc violentNpc = (ViolentNpc) npc;
        if (npc.state() instanceof NpcHurtState hurtState) {
            violentNpc.changeAndStartAI(new ViolentNpcMeleeFightAI(hurtState.attacker(), violentNpc));
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void onActionDone(Npc npc) {
        defaultActionDone(npc);
    }
}
