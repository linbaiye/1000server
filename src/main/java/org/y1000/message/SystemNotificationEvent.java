package org.y1000.message;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;

public final class SystemNotificationEvent extends AbstractPlayerEvent {

    public SystemNotificationEvent(Player source) {
        super(source, true);
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return null;
    }
}
