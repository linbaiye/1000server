package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;
import org.y1000.realm.PlayerEventHandler;

/**
 * A message event that will be sent to visible players.
 */
public abstract class Abstract2VisibleAndSelfMessageEvent extends AbstractClientMessageEvent {
    public Abstract2VisibleAndSelfMessageEvent(Player player, Packet packet) {
        super(player, packet);
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        handler.sendToVisiblePlayersAndSelf(source(), this);
    }
}
