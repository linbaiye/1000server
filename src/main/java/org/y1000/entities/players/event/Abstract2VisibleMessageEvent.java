package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;
import org.y1000.realm.PlayerEventHandler;

public abstract class Abstract2VisibleMessageEvent extends AbstractMessagePlayerEvent {
    public Abstract2VisibleMessageEvent(Player player, Packet packet) {
        super(player, packet);
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        handler.sendToVisiblePlayers(source(), this);
    }
}
