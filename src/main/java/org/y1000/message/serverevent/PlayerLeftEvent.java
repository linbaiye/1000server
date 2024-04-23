package org.y1000.message.serverevent;

import org.y1000.connection.gen.Packet;
import org.y1000.connection.gen.RemoveEntityPacket;
import org.y1000.entities.players.Player;
import org.y1000.message.RemoveEntityMessage;

public class PlayerLeftEvent extends AbstractPlayerEvent {
    public PlayerLeftEvent(Player source) {
        super(source);
    }

    @Override
    protected Packet buildPacket() {
        return RemoveEntityMessage.createPacket(source().id());
    }

    @Override
    protected void accept(PlayerEventHandler playerEventHandler) {
        playerEventHandler.handle(this);
    }
}
