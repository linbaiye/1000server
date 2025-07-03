package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.monster.NpcActionEnum;

public class TurnAction implements NpcAction {
    private final int animationMillis;

    private int elapsedMillis;

    public TurnAction(int animationMillis) {
        this.animationMillis = animationMillis;
    }

    @Override
    public boolean update(int delta) {
        return false;
    }

    public void turn(Npc npc) {

    }

    @Override
    public int elapsedMillis() {
        return elapsedMillis;
    }

    @Override
    public NpcActionEnum actionEnum() {
        return NpcActionEnum.Turn;
    }
}
