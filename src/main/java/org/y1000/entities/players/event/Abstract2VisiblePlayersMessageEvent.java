package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;

/**
 * A message event that will be sent to visible players.
 */
public abstract class Abstract2VisiblePlayersMessageEvent extends AbstractClientMessageEvent {
    public Abstract2VisiblePlayersMessageEvent(Player player, Packet packet) {
        super(player, packet);
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        handler.sendToVisiblePlayers(source(), this);
    }
}
