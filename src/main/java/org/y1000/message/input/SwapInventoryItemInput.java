package org.y1000.message.input;

import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.network.gen.SwapInventorySlotPacket;

public record SwapInventoryItemInput(int slot1, int slot2) implements SelfHandleInput {

    @Override
    public void accept(PlayerInputHandler handler) {
        handler.swapItem(slot1, slot2);
    }

    public static SwapInventoryItemInput fromPacket(SwapInventorySlotPacket packet) {
        return new SwapInventoryItemInput(packet.getSlot1(), packet.getSlot2());
    }
}
