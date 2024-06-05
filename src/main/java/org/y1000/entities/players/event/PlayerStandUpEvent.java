package org.y1000.entities.players.event;

import lombok.Getter;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerStandUpPacket;

@Getter
public final class PlayerStandUpEvent extends AbstractPlayerEvent {

    private final boolean includeSelf;
    public PlayerStandUpEvent(Player source) {
        super(source);
        this.includeSelf = false;
    }

    public PlayerStandUpEvent(Player source, boolean includeSelf) {
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
                .setStandUp(PlayerStandUpPacket.newBuilder().setId(source().id()).build())
                .build();
    }
}
