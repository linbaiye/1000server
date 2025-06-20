package org.y1000.message.input;

import org.y1000.entities.players.Player;

/**
 * An event that interacts with self only.
 */
public interface ClientSelfInteractEvent extends ClientEvent {
    void handle(Player player);
}
