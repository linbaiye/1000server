package org.y1000.message;

import org.y1000.entities.players.Player;

import java.util.Set;

/**
 * A message with a filter that selects players to be sent.
 */
public interface SelectablePlayerMessage extends PlayerMessage {
    Set<Player> select(Set<Player> players);
}
