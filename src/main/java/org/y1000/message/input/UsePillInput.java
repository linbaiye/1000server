package org.y1000.message.input;

import org.y1000.entities.players.PlayerInputHandler;

public record UsePillInput(String name) implements SelfHandleInput {
    @Override
    public void accept(PlayerInputHandler handler) {
        handler.usePill(name);
    }
}
