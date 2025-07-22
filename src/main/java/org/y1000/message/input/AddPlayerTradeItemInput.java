package org.y1000.message.input;

import org.y1000.entities.players.PlayerInputHandler;

public record AddPlayerTradeItemInput(int slot, int number) implements SelfHandleInput {
    @Override
    public void accept(PlayerInputHandler handler) {
        handler.addTradeItem(slot, number);
    }
}
