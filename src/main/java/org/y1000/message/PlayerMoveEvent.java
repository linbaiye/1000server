package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.monster.AbstractMonster;
import org.y1000.entities.players.Player;
import org.y1000.event.EntityEventVisitor;
import org.y1000.util.Coordinate;


public final class PlayerMoveEvent extends AbstractPositionEvent {


    public PlayerMoveEvent(Creature entity, Direction direction, Coordinate coordinate) {
        super(entity, direction, coordinate, entity.stateEnum());
    }

    public static PlayerMoveEvent movingTo(Player player,
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

    public static PlayerMoveEvent movingTo(AbstractMonster monster, Direction direction) {
        return new PlayerMoveEvent(monster, direction, monster.coordinate());
    }
}
