package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerGainExpPacket;

public final class PlayerGainExpEvent extends AbstractPlayerEvent {

    private final String name;

    private final int newLevel;

    public PlayerGainExpEvent(Player source, String name, int newLevel) {
        super(source);
        this.name = name;
        this.newLevel = newLevel;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setGainExp(PlayerGainExpPacket.newBuilder().setName(name).setLevel(newLevel).build())
                .build();
    }
}
