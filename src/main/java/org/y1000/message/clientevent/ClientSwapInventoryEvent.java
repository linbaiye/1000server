package org.y1000.message.clientevent;

import org.y1000.network.gen.SwapInventorySlotPacket;

public record ClientSwapInventoryEvent(int sourceSlot, int destinationSlot) implements ClientInventoryEvent {

    public static ClientSwapInventoryEvent fromPacket(SwapInventorySlotPacket packet) {
        return new ClientSwapInventoryEvent(packet.getSlot1(), packet.getSlot2());
    }
}
