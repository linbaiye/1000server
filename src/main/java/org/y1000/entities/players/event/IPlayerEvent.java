package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.event.EntityEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;

public interface IPlayerEvent extends EntityEvent<Player> {

    default void accept(PlayerEventVisitor playerEventHandler) {
    }
}
