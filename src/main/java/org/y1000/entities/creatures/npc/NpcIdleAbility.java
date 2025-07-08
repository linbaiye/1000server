package org.y1000.entities.creatures.npc;

public final class NpcIdleAbility extends AbstractNonMoveAbility {

    public NpcIdleAbility(NpcAnimation animation) {
        super(animation);
    }

    public void apply(Npc npc) {
        sendActionAndStartAnimation(npc);
    }
}
