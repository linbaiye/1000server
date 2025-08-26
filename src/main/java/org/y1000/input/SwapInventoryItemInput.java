package org.y1000.input;

import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.network.gen.SwapInventorySlotPacket;

public record SwapInventoryItemInput(int from, int to) implements SelfHandleInput {

    @Override
    public void accept(PlayerInputHandler handler) {
        handler.swapItem(from, to);
    }

    public static SwapInventoryItemInput fromPacket(SwapInventorySlotPacket packet) {
        return new SwapInventoryItemInput(packet.getSlot1(), packet.getSlot2());
    }
}
