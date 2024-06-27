package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.AbstractCreatureHurtState;

public final class DisvirtueHurtState extends AbstractCreatureHurtState<Npc> implements NpcState {

    public DisvirtueHurtState(int totalMillis) {
        super(totalMillis);
    }

    @Override
    protected void recovery(Npc npc) {
        npc.onActionDone();
    }
}
