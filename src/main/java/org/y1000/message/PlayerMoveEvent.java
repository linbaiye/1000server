package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.players.MoveAction;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEventVisitor;
import org.y1000.util.Coordinate;


public final class PlayerMoveEvent extends AbstractPositionEvent {

    public PlayerMoveEvent(Creature entity, Direction direction, Coordinate coordinate) {
        super(entity, direction, coordinate, entity.oldStateEnum());
    }

    public PlayerMoveEvent(Creature entity, Direction direction, Coordinate coordinate, MoveAction moveAction) {
        super(entity, direction, coordinate, entity.oldStateEnum(), moveAction);
    }

    public static PlayerMoveEvent movingBy(Player player,
                                           Direction direction,
                                           MoveAction action) {
        return new PlayerMoveEvent(player, direction, player.coordinate(), action);
    }

    public static PlayerMoveEvent movingBy(Player player,
                                           Direction direction) {
        return new PlayerMoveEvent(player, direction, player.coordinate());
    }

    @Override
    protected PositionType getType() {
        return PositionType.MOVE;
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
