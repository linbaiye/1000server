package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.OpenTradeWindowPacket;
import org.y1000.network.gen.Packet;

public final class OpenTradeWindowEvent extends AbstractPlayerEvent {

    private final Integer slot;

    private final long anotherPlayerId;

    public OpenTradeWindowEvent(Player source, long anotherPlayerId, Integer slot) {
        super(source, true);
        this.slot = slot;
        this.anotherPlayerId = anotherPlayerId;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        OpenTradeWindowPacket.Builder builder = OpenTradeWindowPacket
                .newBuilder()
                .setAnotherPlayerId(anotherPlayerId);
        if (slot != null) {
            builder.setSlot(slot);
        }
        return Packet.newBuilder()
                .setOpenTradeWindow(builder)
                .build();
    }
}
