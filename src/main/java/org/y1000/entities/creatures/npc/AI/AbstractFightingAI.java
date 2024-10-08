package org.y1000.entities.creatures.npc.AI;

import org.y1000.entities.creatures.npc.Npc;

public abstract class AbstractFightingAI<N extends Npc> extends AbstractAI<N> {

    private Mover<N> mover;


    @Override
    protected void onActionDoneNotDead(N n) {

    }

    @Override
    protected void onStartNotDead(N n) {

    }
}
