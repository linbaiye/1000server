package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerCooldownPacket;

public class PlayerCooldownEvent extends AbstractPlayerEvent {

    public PlayerCooldownEvent(Player source) {
        super(source);
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder().setCooldown(PlayerCooldownPacket.newBuilder().setId(source().id()).build()).build();
    }

    public static PlayerCooldownEvent of(Player player) {
        return new PlayerCooldownEvent(player);
    }
}
