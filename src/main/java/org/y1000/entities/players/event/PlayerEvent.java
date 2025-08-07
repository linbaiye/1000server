package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.event.TypedEntityEvent;
import org.y1000.realm.PlayerEventHandler;

public interface PlayerEvent extends TypedEntityEvent<Player> {

    void accept(PlayerEventHandler handler);
}
