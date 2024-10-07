package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.AbstractCreatureState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.NpcChangeStateEvent;

public final class NpcCommonState extends AbstractCreatureState<Npc> implements NpcState {

    private final State stat;

    @Override
    public State stateEnum() {
        return stat;
    }

    @Override
    public void update(Npc npc, int delta) {
        if (elapse(delta)) {
            npc.onActionDone();
        }
    }

    public NpcCommonState(int totalMillis, State stat) {
        super(totalMillis);
        this.stat = stat;
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

    @Override
    public void afterHurt(Npc npc) {
        if (stateEnum() == State.ATTACK || elapse(npc.getStateMillis(State.HURT))) {
            npc.onActionDone();
        } else {
            npc.changeState(this);
            npc.emitEvent(NpcChangeStateEvent.of(npc));
        }
    }
}
