package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;

public interface PlayerEvent extends EntityEvent<Player> {

    void accept(PlayerEventHandler handler);
}
