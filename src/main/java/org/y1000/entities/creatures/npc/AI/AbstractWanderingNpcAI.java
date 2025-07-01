package org.y1000.entities.creatures.npc.AI;

import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.entities.creatures.monster.NpcStateEnum;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.util.Coordinate;

@Deprecated
public abstract class AbstractWanderingNpcAI implements NpcAI {

    private Coordinate destination;

    private Coordinate previousCoordinate;


    protected abstract void onHurtDone(Npc npc);

    public AbstractWanderingNpcAI(Coordinate destination, Coordinate previousCoordinate) {
        this.destination = destination ;
        this.previousCoordinate = previousCoordinate;
    }

    public AbstractWanderingNpcAI() {

    }

    private void stayIdle(Npc npc) {
        int stateMillis = npc.getStateMillis(NpcStateEnum.Idle);
        int walkSpeed = npc.walkSpeed();
        int millis = Math.max(walkSpeed, stateMillis) * 2;
        npc.stay(millis);
    }

    protected void defaultActionDone(Npc npc) {
        switch (npc.npcStateEnum()) {
            case Move -> onMoveDone(npc);
            case Idle -> AiPathUtil.moveProcess(npc, destination, previousCoordinate, () -> nextRound(npc),
                    npc.getStateMillis(NpcStateEnum.Move));
            case Turn -> stayIdle(npc);
            case Hurt -> onHurtDone(npc);
            default -> {
                if (!npc.isDead())
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
        if (npc.isDead()) {
            return;
        }
        if (destination == null) {
            destination = npc.wanderingArea().random(npc.spawnCoordinate());
        }
        previousCoordinate = Coordinate.Empty;
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
