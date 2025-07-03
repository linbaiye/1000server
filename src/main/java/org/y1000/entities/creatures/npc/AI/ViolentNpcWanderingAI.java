package org.y1000.entities.creatures.npc.AI;

import org.y1000.entities.creatures.npc.INpc;
import org.y1000.entities.creatures.npc.NpcHurtState;
import org.y1000.entities.creatures.npc.ViolentNpc;
import org.y1000.util.Coordinate;

public final class ViolentNpcWanderingAI extends AbstractWanderingNpcAI {

    public ViolentNpcWanderingAI(Coordinate dest) {
        super(dest, Coordinate.Empty);
    }

    @Override
    protected void onHurtDone(INpc npc) {
        ViolentNpc violentNpc = (ViolentNpc) npc;
        if (npc.npcState() instanceof NpcHurtState hurtState) {
//            violentNpc.changeAndStartAI(new ViolentNpcMeleeFightAI(hurtState.attacker(), violentNpc));
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void onActionDone(INpc npc) {
        defaultActionDone(npc);
    }
}
