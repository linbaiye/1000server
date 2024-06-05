package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.TradeWindowPacket;

public final class OpenTradeWindowEvent extends AbstractPlayerEvent {

    private final Integer slot;

    public OpenTradeWindowEvent(Player source, Integer slot) {
        super(source);
        this.slot = slot;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        TradeWindowPacket.Builder builder = TradeWindowPacket.newBuilder().setOpen(true);
        if (slot != null) {
            builder.setIntputSlot(slot);
        }
        return Packet.newBuilder()
                .setTradeWindow(builder)
                .build();
    }
}
