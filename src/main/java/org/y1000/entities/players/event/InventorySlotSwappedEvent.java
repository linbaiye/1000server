package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.SwapInventorySlotPacket;

public final class InventorySlotSwappedEvent extends AbstractPlayerEvent {
    private final int slot1;
    private final int slot2;

    public InventorySlotSwappedEvent(Player source, int slot1, int slot2) {
        super(source);
        this.slot1 = slot1;
        this.slot2 = slot2;
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {
        playerEventHandler.visit(this);
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder()
                .setSwapInventorySlotPacket(
                        SwapInventorySlotPacket.newBuilder()
                                .setSlot1(slot1)
                                .setSlot2(slot2)
                                .build()
                ).build();

    }
}
