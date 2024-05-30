package org.y1000.entities.players.event;

import lombok.Getter;
import org.y1000.entities.players.Player;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.ClientAttackResponsePacket;
import org.y1000.network.gen.Packet;

import java.util.Optional;

public final class PlayerAttackEventResponse extends AbstractPlayerEvent {

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


    public Optional<PlayerAttackEvent> toPlayerAttackEvent() {
        return accepted ? Optional.of(new PlayerAttackEvent(player(), clientAttackEvent.attackState())) : Optional.empty();
    }


    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }
}
