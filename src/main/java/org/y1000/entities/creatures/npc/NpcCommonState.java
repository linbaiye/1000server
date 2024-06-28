package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.AbstractNpcState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.NpcChangeStateEvent;

public final class NpcCommonState extends AbstractNpcState<Npc> implements NpcState {

    public NpcCommonState(int totalMillis, State stat) {
        super(totalMillis, stat);
    }

    @Override
    protected void nextMove(Npc npc) {
        npc.onActionDone();
    }

    @Override
    public boolean attackable() {
        return stateEnum() != State.DIE;
    }

    public static NpcCommonState idle(int total) {
        return new NpcCommonState(total, State.IDLE);
    }

    public static NpcCommonState attack(int total) {
        return new NpcCommonState(total, State.ATTACK);
    }

    public static NpcCommonState die(int total) {
        return new NpcCommonState(total, State.DIE);
    }

    public static NpcCommonState freeze(int total) {
        return new NpcCommonState(total, State.FROZEN);
    }

    @Override
    public void afterHurt(Npc npc) {
        if (stateEnum() == State.ATTACK || elapse(npc.getStateMillis(State.HURT))) {
            nextMove(npc);
        } else {
            npc.changeAction(this);
            npc.emitEvent(NpcChangeStateEvent.of(npc));
        }
    }
}
