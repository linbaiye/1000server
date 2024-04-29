package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.ClientAttackResponsePacket;
import org.y1000.network.gen.Packet;

public class PlayerAttackEventResponse extends AbstractPlayerEvent {

    private final long seq;
    private final boolean ok;
    public PlayerAttackEventResponse(Player source, long seq, boolean ok) {

        super(source);
        this.seq = seq;
        this.ok = ok;
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setAttackEventResponsePacket(ClientAttackResponsePacket.newBuilder()
                        .setAccepted(ok)
                        .setSequence(seq)
                        .build())
                .build();
    }

    @Override
    protected void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }
}
