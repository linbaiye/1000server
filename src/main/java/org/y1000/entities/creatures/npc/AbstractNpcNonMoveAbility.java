package org.y1000.entities.creatures.npc;

import org.y1000.entities.creatures.npc.event.NpcStartActionEvent;
import org.y1000.message.NpcSnapshot;

abstract class AbstractNpcNonMoveAbility extends AbstractNpcAbility {

    public AbstractNpcNonMoveAbility(NpcAnimation animation) {
        super(animation);
    }

    void sendActionAndStartAnimation(Npc npc) {
        npc.sendEvent(NpcStartActionEvent.of(npc, type()));
        startAnimation();
    }

    void sendActionAndStartShortAnimation(Npc npc, int millis) {
        if (millis > 0)
            npc.sendEvent(NpcStartActionEvent.of(npc, type()));
        startShorter(millis);
    }

    @Override
    public NpcSnapshot captureSnapshot(Npc npc) {
        return NpcSnapshot.of(npc, getAnimation().elapsedMillis(), getAnimation().type());
    }
}
