package org.y1000.entities.creatures.npc.AI;

import org.slf4j.Logger;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.creatures.npc.NpcFrozenAI;

import java.util.function.Consumer;

public abstract class AbstractAI<N extends Npc> implements NpcAI {

    protected abstract void onStartNotDead(N n);

    protected abstract Class<N> npcType();

    protected abstract void onActionDoneNotDead(N n);

    protected abstract void onMoveFailedNotDead(N n);

    private void invokeIfNoDead(Npc npc, Consumer<N> consumer) {
        if (npc == null || npc.stateEnum() == State.DIE) {
            if (npc != null)
                npc.changeAndStartAI(NpcFrozenAI.INSTANCE);
            return;
        }
        Class<N> type = npcType();
        if (type.isAssignableFrom(npc.getClass())) {
            consumer.accept(type.cast(npc));
        }
    }

    protected abstract Logger log();

    @Override
    public void onActionDone(Npc npc) {
        invokeIfNoDead(npc, this::onActionDoneNotDead);
    }

    @Override
    public void onMoveFailed(Npc npc) {
        invokeIfNoDead(npc, this::onMoveFailedNotDead);
    }

    @Override
    public void start(Npc npc) {
        invokeIfNoDead(npc, this::onStartNotDead);
    }
}
