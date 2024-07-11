package org.y1000.entities.creatures.npc;

public final class DoNothingAI implements NpcAI {
    public static final DoNothingAI INSTANCE = new DoNothingAI();

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
