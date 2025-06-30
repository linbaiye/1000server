package org.y1000.message;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;

import java.util.Set;
import java.util.stream.Collectors;

public class AbstractInsightNoSelfPlayerMessage extends AbstractPlayerMessage implements SelectablePlayerMessage {
    public AbstractInsightNoSelfPlayerMessage(Player player, Packet packet) {
        super(player, packet);
    }

    @Override
    public Set<Player> select(Set<Player> players) {
        return players.stream().filter(p -> !p.equals(source()) && p.canBeSeenAt(source().coordinate()))
                .collect(Collectors.toSet());
    }
}
