package org.y1000.entities.creatures.npc.AI;

import org.y1000.entities.creatures.event.SeekAggressiveMonsterEvent;
import org.y1000.entities.creatures.npc.AI.AbstractWanderingNpcAI;
import org.y1000.entities.creatures.npc.AI.ViolentNpcMeleeFightAI;
import org.y1000.entities.creatures.npc.Guardian;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcHurtState;
import org.y1000.util.Coordinate;

public final class GuardWanderingAI extends AbstractWanderingNpcAI {

    public GuardWanderingAI(Coordinate dest) {
        super(dest, Coordinate.Empty);
    }

    @Override
    protected void onHurtDone(Npc npc) {
        if (!(npc instanceof Guardian guardian)) {
            return;
        }
        if (npc.state() instanceof NpcHurtState hurtState) {
            guardian.changeAndStartAI(new ViolentNpcMeleeFightAI(hurtState.attacker(), guardian));
        }
    }

    @Override
    public void onActionDone(Npc npc) {
        if ((npc instanceof Guardian guardian)) {
            guardian.emitEvent(new SeekAggressiveMonsterEvent(guardian, guardian.getWidth()));
            if (guardian.getAI() != this) {
                return;
            }
        }
        defaultActionDone(npc);
    }
}
