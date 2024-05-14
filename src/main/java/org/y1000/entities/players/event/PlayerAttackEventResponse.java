package org.y1000.entities.players.event;

import lombok.Getter;
import org.y1000.entities.players.Player;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.ClientAttackResponsePacket;
import org.y1000.network.gen.Packet;

public class PlayerAttackEventResponse extends AbstractPlayerEvent {

    private final ClientAttackEvent clientAttackEvent;

    @Getter
    private final boolean accepted;

    public PlayerAttackEventResponse(Player source, ClientAttackEvent clientEvent, boolean ok) {
        super(source);
        clientAttackEvent = clientEvent;
        this.accepted = ok;
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setAttackEventResponsePacket(ClientAttackResponsePacket.newBuilder()
                        .setAccepted(accepted)
                        .setSequence(clientAttackEvent.sequence())
                        .build())
                .build();
    }


    public PlayerAttackEvent toPlayerAttackEvent() {
        return new PlayerAttackEvent(player(), clientAttackEvent.attackState());
    }


    @Override
    protected void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }
}
