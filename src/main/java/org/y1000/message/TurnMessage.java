package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

public class TurnMessage extends AbstractPositionMessage {

    public TurnMessage(long id, Direction direction, Coordinate coordinate) {
        super(id, direction, coordinate);
    }

    public static TurnMessage fromPlayer(Player p) {
        return new TurnMessage(p.id(), p.direction(), p.coordinate());
    }

    @Override
    protected PositionType getType() {
        return PositionType.TURN;
    }
}
