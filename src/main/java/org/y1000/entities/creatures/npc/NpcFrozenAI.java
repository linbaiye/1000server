package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.npc.AI.NpcAI;

public final class NpcFrozenAI implements NpcAI {
    public static final NpcFrozenAI INSTANCE = new NpcFrozenAI();
    private NpcFrozenAI() {}

    @Override
    public void onActionDone(Npc npc) {

    }

    @Override
    public void onMoveFailed(Npc npc) {

    }

    @Override
    public void start(Npc npc) {

    }
}
