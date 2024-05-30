package org.y1000.message.serverevent;

import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.network.gen.Packet;
import org.y1000.entities.players.Player;
import org.y1000.message.RemoveEntityMessage;

public final class PlayerLeftEvent extends AbstractPlayerEvent {
    public PlayerLeftEvent(Player source) {
        super(source);
    }

    @Override
    protected Packet buildPacket() {
        return RemoveEntityMessage.createPacket(source().id());
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }
}
