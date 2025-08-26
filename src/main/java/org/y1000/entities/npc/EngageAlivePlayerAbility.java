package org.y1000.entities.npc;

import org.y1000.entities.FilterVisibleEvent;
import org.y1000.entities.players.Player;

import java.util.Comparator;
import java.util.Optional;

public class EngageAlivePlayerAbility {

    private final int viewRange;

    public EngageAlivePlayerAbility(int viewRange) {
        this.viewRange = viewRange;
    }

    public Optional<Player> find(Npc npc) {
        var event = FilterVisibleEvent.nearbyAttackable(npc, viewRange);
        npc.sendEvent(event);
        return event.resultStream(Player.class)
                .min(Comparator.comparingInt(o -> npc.coordinate().directDistance(o.coordinate())));
    }
}
