package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.event.EntityEventVisitor;
import org.y1000.message.serverevent.PlayerEventVisitor;

public interface PlayerEvent extends EntityEvent {

    default Player player() {
        return (Player) source();
    }

    default void accept(PlayerEventVisitor playerEventHandler) {

    }

    default void accept(EntityEventVisitor visitor) {
        if (visitor instanceof PlayerEventVisitor playerEventHandler) {
            accept(playerEventHandler);
        }
    }
}
