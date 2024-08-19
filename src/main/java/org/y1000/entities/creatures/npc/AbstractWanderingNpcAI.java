package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.AiPathUtil;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;

import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractWanderingNpcAI implements NpcAI {

    private Coordinate destination;

    private Coordinate previousCoordinate;


    protected abstract void onHurtDone(Npc npc);

    public AbstractWanderingNpcAI(Coordinate destination, Coordinate previousCoordinate) {
        this.destination = destination;
        this.previousCoordinate = previousCoordinate;
    }

    public AbstractWanderingNpcAI() {

    }


    private void stayIdle(Npc npc) {
        int stateMillis = npc.getStateMillis(State.IDLE) ;
        int walkSpeed = npc.walkSpeed();
        int millis = Math.max(walkSpeed, stateMillis) * 2;
        npc.stay(millis);
    }

    @Override
    public void onActionDone(Npc npc) {
        switch (npc.stateEnum()) {
            case WALK -> onMoveDone(npc);
            case IDLE -> AiPathUtil.moveProcess(npc, destination, previousCoordinate, () -> nextRound(npc),
                    npc.getStateMillis(State.WALK), npc.getStateMillis(State.IDLE));
            case HURT -> onHurtDone(npc);
            default -> {
                if (npc.stateEnum() != State.DIE)
                    nextRound(npc);
            }
        }
    }

    @Override
    public void onMoveFailed(Npc npc) {
        nextRound(npc);
    }

    private void nextRound(Npc npc) {
        previousCoordinate = null;
        destination = null;
        start(npc);
    }

    @Override
    public void start(Npc npc) {
        if (npc.stateEnum() == State.DIE) {
            return;
        }
        if (destination == null) {
            previousCoordinate = npc.coordinate();
            destination = npc.wanderingArea().random(npc.spawnCoordinate());
        }
        stayIdle(npc);
    }

    private void onMoveDone(Npc npc) {
        previousCoordinate = npc.coordinate().moveBy(npc.direction().opposite());
        if (npc.coordinate().equals(destination)) {
            nextRound(npc);
        } else {
            stayIdle(npc);
        }
    }
}
