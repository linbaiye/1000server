package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.npc.event.NpcStartActionEvent;

abstract class AbstractNonMoveAbility extends AbstractNpcAbility {

    public AbstractNonMoveAbility(NpcAnimation animation) {
        super(animation);
    }

    void sendActionAndStartAnimation(Npc npc) {
        npc.sendEvent(NpcStartActionEvent.of(npc, type()));
        startAnimation();
    }

    @Override
    public boolean update(int delta) {
        return updateAnimation(delta);
    }
}
