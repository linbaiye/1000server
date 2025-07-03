package org.y1000.entities.creatures.npc;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.monster.NpcActionEnum;

public class IdleAction implements NpcAction {

    private final int animationMillis;

    private int elapsedMillis;

    private int elapsedMillis;


    public IdleAction(int animationMillis) {
        this.animationMillis = animationMillis;
    }

    @Override
    public boolean update(int delta) {

    }

    @Override
    public int elapsedMillis() {
        return 0;
    }

    @Override
    public NpcActionEnum actionEnum() {
        return NpcActionEnum.Idle;
    }

    public void stay(Direction direction) {

    }


}
