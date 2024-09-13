package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.event.SeekAggressiveMonsterEvent;

public final class GuardWanderingAI extends AbstractWanderingNpcAI {

    @Override
    protected void onHurtDone(Npc npc) {
        if (!(npc instanceof Guardian guardian)) {
            return;
        }
        if (npc.state() instanceof NpcHurtState hurtState) {
            guardian.changeAI(new ViolentNpcMeleeFightAI(hurtState.attacker(), guardian));
        }
    }

    @Override
    public void onActionDone(Npc npc) {
        if ((npc instanceof Guardian guardian)) {
            guardian.emitEvent(new SeekAggressiveMonsterEvent(guardian, guardian.getWidth()));
            if (guardian.getAi() != this) {
                return;
            }
        }
        defaultActionDone(npc);
    }
}
