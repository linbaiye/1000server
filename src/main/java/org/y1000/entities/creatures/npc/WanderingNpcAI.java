package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.monster.*;
import org.y1000.message.SetPositionEvent;
import org.y1000.util.Coordinate;


public final class WanderingNpcAI implements NpcAI {
    private Coordinate destination;

    private Coordinate previousCoordinate;

    public WanderingNpcAI(Coordinate destination,
                              Coordinate previousCoordinate) {
        this.destination = destination;
        this.previousCoordinate = previousCoordinate;
    }

    public void onMoveFailed(Npc npc) {
        npc.emitEvent(SetPositionEvent.of(npc));
        npc.changeState(NpcCommonState.idle(npc.getStateMillis(State.IDLE)));
    }

    @Override
    public void onHurtDone(Npc npc) {
    }

    @Override
    public void onIdleDone(Npc npc) {

    }

    @Override
    public void onMoveDone(Npc npc) {

    }

    @Override
    public void onFrozenDone(Npc npc) {

    }

    private void nextRound(AbstractMonster monster) {
        destination = monster.wanderingArea().random(monster.spawnCoordinate());
        previousCoordinate = monster.coordinate();
    }


    @Override
    public void start(Npc npc) {
        npc.changeState(NpcCommonState.idle(npc.getStateMillis(State.IDLE)));
    }
}
