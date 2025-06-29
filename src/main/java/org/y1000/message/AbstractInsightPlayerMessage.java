package org.y1000.message;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractInsightPlayerMessage extends AbstractPlayerMessage implements SelectablePlayerMessage {

    public AbstractInsightPlayerMessage(Player player, Packet packet) {
        super(player, packet);
    }

    @Override
    public Set<Player> select(Set<Player> players) {
        return players.stream()
                .filter(p -> source().canBeSeenAt(p.coordinate()))
                .collect(Collectors.toSet());
    }
}
