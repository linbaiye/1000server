package org.y1000.entities.creatures.npc.event;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.players.Player;
import org.y1000.realm.NpcEventHandler;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterNearbyPlayerEvent extends AbstractNpcEvent implements FilterVisibleEntityEvent {

    private Set<Player> players;

    private final int distance;

    public FilterNearbyPlayerEvent(Npc npc, int distance) {
        super(npc);
        this.distance = distance;
    }

    @Override
    public void filter(Set<Entity> visibleEntities) {
        players = visibleEntities.stream()
                .filter(e -> e instanceof Player && e.coordinate().directDistance(source().coordinate()) <= distance)
                .map(Player.class::cast)
                .collect(Collectors.toSet());
    }

    @Override
    public void accept(NpcEventHandler handler) {
        handler.filter(this);
    }

    public Set<Player> players() {
        return players != null ? players : Collections.emptySet();
    }

    public static FilterVisibleEntityEvent withinDistance(Npc npc, int d) {
        return new FilterNearbyPlayerEvent(npc, d);
    }

}
