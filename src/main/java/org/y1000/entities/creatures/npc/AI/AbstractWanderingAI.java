package org.y1000.entities.creatures.npc.AI;

import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.util.Coordinate;


public abstract class AbstractWanderingAI<N extends Npc> extends AbstractAI<N> {
    private Mover<N> mover;

    @Override
    protected void onStartNotDead(N npc) {
        if (mover == null) {
            mover = Mover.walk(npc, random(npc));
            mover.walk(this::moveToNextRandom);
        } else {
            continueWander(npc);
        }
    }

    @Override
    protected void onMoveFailedNotDead(N npc) {
        moveToNextRandom(npc);
    }

    private void moveToNextRandom(N npc) {
        log().debug("Next random.");
        mover.changeDestination(random(npc));
        npc.stay(npc.getStateMillis(State.IDLE));
    }

    protected abstract Coordinate random(Npc npc);


    protected void continueWander(N npc) {
        if (mover.isArrived()) {
            moveToNextRandom(npc);
        } else {
            mover.walk(this::moveToNextRandom);
        }
    }
}
