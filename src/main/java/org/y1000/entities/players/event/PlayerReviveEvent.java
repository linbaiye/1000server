package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerRevivePacket;

public class PlayerReviveEvent extends AbstractPlayerEvent {
    public PlayerReviveEvent(Player source) {
        super(source);
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder().setPlayerRevive(PlayerRevivePacket.newBuilder()
                .setId(source().id()).build()).build();
    }
}
