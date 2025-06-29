package org.y1000.message;

import org.y1000.entities.players.Player;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractAOIClientMessage implements SelectablePlayerMessage {

    private final Player source;

    protected AbstractAOIClientMessage(Player source) {
        this.source = source;
    }

    @Override
    public Set<Player> select(Set<Player> players) {
        return players.stream().filter(p -> source.canBeSeenAt(source.coordinate()))
                .collect(Collectors.toSet());
    }
}
