package org.y1000.message;

import org.y1000.entities.players.Player;
import org.y1000.entities.players.event.AbstractPlayerEvent;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.BreakRopePacket;
import org.y1000.network.gen.Packet;

/**
 * Notify player of rope breaking in order to modify camera.
 */
public final class BreakRopeEvent extends AbstractPlayerEvent {

    private final Packet packet;
    public BreakRopeEvent(Player source) {
        super(source, true);
        packet = Packet.newBuilder()
                .setBreakRope(BreakRopePacket.newBuilder()
                        .setId(source.id()).build())
                .build();
    }
    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }
}
