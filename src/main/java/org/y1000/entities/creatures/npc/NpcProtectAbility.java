package org.y1000.entities.creatures.npc;

import org.y1000.entities.FilterVisibleEvent;

import java.util.Optional;

public class NpcProtectAbility {

    private final int viewRange;

    public NpcProtectAbility(int viewRange) {
        this.viewRange = viewRange;
    }

    public Optional<Npc> findEnemy(Npc npc) {
        var event = FilterVisibleEvent.nearbyAttackable(npc, viewRange);
        npc.sendEvent(event);
        return event.resultStream(Npc.class).filter(e -> e.findAbility(EngageAlivePlayerAbility.class).isPresent()).findFirst();
    }
}
