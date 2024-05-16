package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.PassiveMonster;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.util.Coordinate;


public final class MoveEvent extends AbstractPositionEvent {

    public MoveEvent(Creature entity, Direction direction, Coordinate coordinate) {
        super(entity, direction, coordinate);
    }

    public static MoveEvent movingTo(Player player,
                                     Direction direction) {
        return new MoveEvent(player, direction, player.coordinate());
    }

    @Override
    protected PositionType getType() {
        return PositionType.MOVE;
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }

    public static MoveEvent movingTo(PassiveMonster monster, Direction direction) {
        return new MoveEvent(monster, direction, monster.coordinate());
    }
}
