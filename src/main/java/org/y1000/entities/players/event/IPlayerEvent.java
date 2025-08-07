package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.event.TypedEntityEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;

public interface IPlayerEvent extends TypedEntityEvent<Player> {

    default void accept(PlayerEventVisitor playerEventHandler) {
    }
}
