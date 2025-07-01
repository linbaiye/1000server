package org.y1000.message.input;

import org.y1000.entities.Direction;
import org.y1000.entities.players.PlayerInputHandler;
import org.y1000.util.Coordinate;

public record MoveInput(Coordinate from, Direction direction) implements SelfHandleInput {
    @Override
    public void accept(PlayerInputHandler handler) {
        handler.move(this);
    }

    public Coordinate destination() {
        return from.moveBy(direction);
    }
}
