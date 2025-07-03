package org.y1000.entities.creatures.npc.AI;

import org.y1000.entities.creatures.npc.INpc;

public abstract class AbstractFightingAI<N extends INpc> extends AbstractAI<N> {

    private Mover<N> mover;


    @Override
    protected void onActionDoneNotDead(N n) {

    }

    @Override
    protected void onStartNotDead(N n) {

    }
}
