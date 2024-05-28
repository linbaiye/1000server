package org.y1000.message.serverevent;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.network.gen.Packet;

public final class GainItemEvent extends AbstractPlayerEvent {

    public GainItemEvent(Player source) {
        super(source);
    }

    @Override
    protected void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return null;
    }
}
