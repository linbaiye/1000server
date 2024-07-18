package org.y1000.entities.players.event;

import lombok.Getter;
import org.y1000.entities.creatures.State;
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

    private final State backToState;

    private final Integer effectId;

    public PlayerAttackEventResponse(Player source, ClientAttackEvent clientEvent, boolean ok, Integer effectId) {
        super(source);
        clientAttackEvent = clientEvent;
        this.accepted = ok;
        backToState = source.stateEnum();
        this.effectId = effectId;
    }

    public PlayerAttackEventResponse(Player source, ClientAttackEvent clientEvent, boolean ok) {
        this(source, clientEvent, ok, null);
    }

    @Override
    protected Packet buildPacket() {
        ClientAttackResponsePacket.Builder builder = ClientAttackResponsePacket.newBuilder()
                .setAccepted(accepted)
                .setSequence(clientAttackEvent.sequence());
        if (!accepted) {
            builder.setBackToState(backToState.value());
        }
        return Packet.newBuilder()
                .setAttackEventResponsePacket(builder)
                .build();
    }


    public Optional<PlayerAttackEvent> toPlayerAttackEvent() {
        return accepted ? Optional.of(new PlayerAttackEvent(player(), clientAttackEvent.attackState(), effectId)) : Optional.empty();
    }


    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }
}
