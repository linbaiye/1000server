package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

public class SetPositionMessage extends AbstractPositionMessage {

    public SetPositionMessage(long id, Direction direction, Coordinate coordinate) {
        super(id, direction, coordinate);
    }

    @Override
    protected PositionType getType() {
        return PositionType.SET;
    }

    public static SetPositionMessage fromPlayer(Player player) {
        return new SetPositionMessage(player.id(), player.direction(), player.coordinate());
    }
}
