package org.y1000.entities.creatures.npc.AI;

import org.slf4j.Logger;
import org.y1000.entities.creatures.npc.INpc;
import org.y1000.entities.creatures.npc.NpcFrozenAI;

import java.util.function.Consumer;

public abstract class AbstractAI<N extends INpc> implements INpcAI {

    protected abstract void onStartNotDead(N n);

    protected abstract Class<N> npcType();

    protected abstract void onActionDoneNotDead(N n);

    protected abstract void onMoveFailedNotDead(N n);

    private void invokeIfNoDead(INpc npc, Consumer<N> consumer) {
        if (npc == null || npc.isDead()) {
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
    public void onActionDone(INpc npc) {
        invokeIfNoDead(npc, this::onActionDoneNotDead);
    }

    @Override
    public void onMoveFailed(INpc npc) {
        invokeIfNoDead(npc, this::onMoveFailedNotDead);
    }

    @Override
    public void start(INpc npc) {
        invokeIfNoDead(npc, this::onStartNotDead);
    }
}
