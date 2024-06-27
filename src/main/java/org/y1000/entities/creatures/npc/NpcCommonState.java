package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.AbstractNpcState;
import org.y1000.entities.creatures.State;

public final class NpcCommonState extends AbstractNpcState<Npc> implements NpcState {

    public NpcCommonState(int totalMillis, State stat) {
        super(totalMillis, stat);
    }
    @Override
    protected void nextMove(Npc npc) {
        npc.onActionDone();
    }

    public static NpcCommonState idle(int total) {
        return new NpcCommonState(total, State.IDLE);
    }

    public static NpcCommonState freeze(int total) {
        return new NpcCommonState(total, State.FROZEN);
    }
}
