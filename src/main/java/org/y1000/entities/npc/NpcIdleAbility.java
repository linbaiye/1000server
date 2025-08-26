package org.y1000.entities.npc;

import org.y1000.entities.npc.event.NpcStartActionEvent;

public final class NpcIdleAbility extends AbstractNpcNonMoveAbility {

    public NpcIdleAbility(NpcAnimation animation) {
        super(animation);
    }

    public void apply(Npc npc) {
        sendActionAndStartAnimation(npc);
    }

    public void apply(Npc npc, int millis) {
        npc.sendEvent(NpcStartActionEvent.of(npc, type()));
        startAnimation(millis);
    }

    @Override
    public boolean update(int delta) {
        return updateAnimation(delta);
    }
}
