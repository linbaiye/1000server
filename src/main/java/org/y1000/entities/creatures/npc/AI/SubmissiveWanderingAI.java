package org.y1000.entities.creatures.npc.AI;


import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcHurtState;
import org.y1000.util.Coordinate;

public final class SubmissiveWanderingAI extends AbstractWanderingNpcAI {

    private final Chatter chatter;

    @Override
    protected void onHurtDone(Npc npc) {
        if (npc.state() instanceof NpcHurtState hurtState) {
            npc.changeState(hurtState.previousState());
            npc.state().afterHurt(npc);
        } else {
            throw new IllegalStateException();
        }
    }

    public SubmissiveWanderingAI(Coordinate destination, Coordinate previousCoordinate) {
        super(destination, previousCoordinate);
        chatter = null;
    }

    public SubmissiveWanderingAI() {
        this(null);
    }

    public SubmissiveWanderingAI(Chatter chatter) {
        this.chatter = chatter;
    }

    @Override
    public void onActionDone(Npc npc) {
        if (chatter != null)
            chatter.onActionDone(npc);
        defaultActionDone(npc);
    }
}
