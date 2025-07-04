package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;

/**
 * A message event that will be sent to the source() player.
 */
public abstract class Abstract2PlayerMessageEvent extends AbstractClientMessageEvent {

    public Abstract2PlayerMessageEvent(Player player, Packet packet) {
        super(player, packet);
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        handler.sendTo(source(), this);
    }
}
