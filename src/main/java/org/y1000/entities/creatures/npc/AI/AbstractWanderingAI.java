package org.y1000.entities.creatures.npc.AI;

import org.y1000.entities.creatures.npc.Npc;
import org.y1000.util.Coordinate;


public abstract class AbstractWanderingAI<N extends Npc> extends AbstractAI<N> {
    private Mover<N> mover;

    @Override
    protected void onStartNotDead(N npc) {
        if (mover == null) {
            mover = Mover.walk(npc, npc.wanderingArea().random(npc.spawnCoordinate()));
            mover.nextMove(this::moveToNextRandom);
        } else {
            continueWander(npc);
        }
    }

    @Override
    protected void onMoveFailedNotDead(N npc) {
        moveToNextRandom(npc);
    }

    private void moveToNextRandom(N npc) {
        mover.changeDestination(randomPoint(npc));
        mover.nextMove(this::onDeadEnd);
    }

    private void onDeadEnd(N npc) {
        npc.stay(500);
    }

    private Coordinate randomPoint(N npc) {
        return npc.wanderingArea().random(npc.spawnCoordinate());
    }

    protected void continueWander(N npc) {
        if (mover.isArrived()) {
            moveToNextRandom(npc);
        } else {
            mover.nextMove(this::moveToNextRandom);
        }
    }
}
