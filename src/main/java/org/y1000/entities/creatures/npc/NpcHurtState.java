package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.AbstractCreatureHurtState;
import org.y1000.entities.creatures.Creature;

public final class NpcHurtState extends AbstractCreatureHurtState<Npc> implements NpcState {
    /**
     * The state before getting hurt.
     */
    private final NpcState previousState;

    private final Creature attacker;

    public NpcHurtState(int totalMillis, NpcState previousState, Creature attacker) {
        super(totalMillis);
        this.attacker = attacker;
        if (previousState instanceof NpcHurtState hurtState)
            this.previousState = hurtState.previousState;
        else
            this.previousState = previousState;
    }

    public Creature attacker() {
        return attacker;
    }

    protected void recovery(Npc npc) {
        npc.onActionDone();
    }

    public NpcState previousState() {
        return previousState;
    }

    @Override
    public void afterHurt(Npc npc) {
        recovery(npc);
    }
}
