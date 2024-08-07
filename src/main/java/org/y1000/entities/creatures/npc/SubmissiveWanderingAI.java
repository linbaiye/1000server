package org.y1000.entities.creatures.npc;


import org.y1000.util.Coordinate;

public final class SubmissiveWanderingAI extends AbstractWanderingNpcAI {

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
    }

    public SubmissiveWanderingAI() {
    }
}
