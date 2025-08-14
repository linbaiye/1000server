package org.y1000.input;

import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.network.gen.CreateGuildInputPacket;

public record CreateGuildInput(boolean confirmed, int slotId, String name) implements SelfHandleInput {

    public static CreateGuildInput fromPacket(CreateGuildInputPacket packet){
        return new CreateGuildInput(packet.getConfirm(),  packet.getFromSlot(), packet.getName());
    }

    @Override
    public void accept(PlayerInputHandler handler) {
        if (confirmed)
            handler.confirmGuildCreation(slotId, name);
        else
            handler.cancelGuildCreation();
    }
}
