package org.y1000.message.input;

import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.network.gen.ClickKungFuInputPacket;

public class ClickKungFuInput extends AbstractClickContainerSlotInput {
    private final int page;

    public ClickKungFuInput(ClickType clickType, int page, int slot) {
        super(clickType, slot);
        this.page = page;
    }

    @Override
    public void accept(PlayerInputHandler handler) {
        handler.onKungFuClicked(page, slot, clickType);
    }

    public static ClickKungFuInput fromPacket(ClickKungFuInputPacket packet) {
        return new ClickKungFuInput(ClickType.type(packet.getClickType()), packet.getPage(), packet.getSlot());
    }
}
