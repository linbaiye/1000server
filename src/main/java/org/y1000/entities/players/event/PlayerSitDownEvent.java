package org.y1000.entities.players.event;

import lombok.Getter;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerSitDownPacket;

@Getter
public final class PlayerSitDownEvent extends AbstractPlayerEvent {

    private final boolean includeSelf;

    public PlayerSitDownEvent(Player source) {
        this(source, false);
    }
    public PlayerSitDownEvent(Player source, boolean includeSelf) {
        super(source);
        this.includeSelf = includeSelf;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setSitDown(PlayerSitDownPacket.newBuilder().setId(source().id()))
                .build();
    }
}
