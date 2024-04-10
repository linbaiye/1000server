package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

public class TurnEvent extends AbstractPositionEvent {

    public TurnEvent(Entity entity, Direction direction, Coordinate coordinate) {
        super(entity, direction, coordinate);
    }

    public static TurnEvent fromPlayer(Player p) {
        return new TurnEvent(p, p.direction(), p.coordinate());
    }

    @Override
    protected PositionType getType() {
        return PositionType.TURN;
    }

    @Override
    public void accept(ServerEventVisitor visitor) {
        visitor.visit(this);
    }
}
