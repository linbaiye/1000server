package org.y1000.input;

import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.network.gen.ClickInventorySlotInputPacket;

public final class ClickInventorySlotInput extends AbstractClickContainerSlotInput {

    public ClickInventorySlotInput(ClickType clickType, int slot) {
        super(clickType, slot);
    }

    @Override
    public void accept(PlayerInputHandler handler) {
        handler.onInventorySlotClicked(slot, clickType);
    }

    public static ClickInventorySlotInput fromPacket(ClickInventorySlotInputPacket packet) {
        return new ClickInventorySlotInput(ClickType.type(packet.getClickType()), packet.getSlot());
    }
}
