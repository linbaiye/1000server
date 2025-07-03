package org.y1000.entities.creatures.npc;

public class FightAI implements NpcAI {
    private final Npc npc;

    public FightAI(Npc npc) {
        this.npc = npc;
    }


    @Override
    public void update(int delta) {

    }

    @Override
    public NpcAction currentAction() {
        return null;
    }
}
