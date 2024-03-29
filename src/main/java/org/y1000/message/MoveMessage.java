package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

public class MoveMessage extends AbstractPositionMessage {

    public MoveMessage(long id, Direction direction, Coordinate coordinate) {
        super(id, direction, coordinate);
    }

    public static MoveMessage fromPlayer(Player player) {
        return new MoveMessage(player.id(), player.direction(), player.coordinate());
    }

    @Override
    protected PositionType getType() {
        return PositionType.MOVE;
    }
}
