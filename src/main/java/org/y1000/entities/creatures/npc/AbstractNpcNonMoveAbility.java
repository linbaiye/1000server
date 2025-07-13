package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.npc.event.NpcStartActionEvent;

abstract class AbstractNpcNonMoveAbility extends AbstractNpcAbility {

    public AbstractNpcNonMoveAbility(NpcAnimation animation) {
        super(animation);
    }

    void sendActionAndStartAnimation(Npc npc) {
        npc.sendEvent(NpcStartActionEvent.of(npc, type()));
        startAnimation();
    }

    void sendActionAndStartShortAnimation(Npc npc, int millis) {
        npc.sendEvent(NpcStartActionEvent.of(npc, type()));
        startShorter(millis);
    }
}
