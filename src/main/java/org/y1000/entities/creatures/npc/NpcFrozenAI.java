package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.npc.AI.INpcAI;

public final class NpcFrozenAI implements INpcAI {
    public static final NpcFrozenAI INSTANCE = new NpcFrozenAI();
    private NpcFrozenAI() {}

    @Override
    public void onActionDone(INpc npc) {

    }

    @Override
    public void onMoveFailed(INpc npc) {

    }

    @Override
    public void start(INpc npc) {

    }
}
