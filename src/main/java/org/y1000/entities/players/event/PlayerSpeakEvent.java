package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;

public class PlayerSpeakEvent extends AbstractPlayerEvent {
    private final Packet packet;
    public PlayerSpeakEvent(Player source, String content) {
        super(source);
        packet = null;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return null;
    }
}
