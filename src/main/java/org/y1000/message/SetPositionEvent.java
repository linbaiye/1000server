package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.util.Coordinate;

public class SetPositionEvent extends AbstractPositionEvent {

    public SetPositionEvent(Entity entity, Direction direction, Coordinate coordinate) {
        super(entity, direction, coordinate);
    }

    @Override
    protected MovementType getType() {
        return MovementType.SET;
    }

    public static SetPositionEvent fromPlayer(Player player) {
        return new SetPositionEvent(player, player.direction(), player.coordinate());
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
