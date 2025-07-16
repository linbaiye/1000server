package org.y1000.message.input;

import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.network.gen.SwapKungFuSlotPacket;

public record SwapKungFuInput(int page, int slot1, int slot2) implements SelfHandleInput {
    @Override
    public void accept(PlayerInputHandler handler) {
        handler.swapKungFu(page, slot1, slot2);
    }

    public static SwapKungFuInput fromPacket(SwapKungFuSlotPacket packet) {
        return new SwapKungFuInput(packet.getPage(), packet.getSlot1(), packet.getSlot2());
    }
}
