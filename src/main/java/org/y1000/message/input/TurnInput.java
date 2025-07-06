package org.y1000.message.input;

import org.y1000.entities.Direction;
import org.y1000.entities.players.PlayerInputHandler;

public record TurnInput(Direction direction) implements SelfHandleInput {
    @Override
    public void accept(PlayerInputHandler handler) {
        handler.turn(this);
    }
}
