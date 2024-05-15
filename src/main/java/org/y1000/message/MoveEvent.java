package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.PassiveMonster;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.kungfu.FootKungFu;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.util.Coordinate;

import java.util.Optional;

public final class MoveEvent extends AbstractPositionEvent {

    private final PositionType positionType;

    public MoveEvent(Creature entity, Direction direction, Coordinate coordinate, PositionType positionType) {
        super(entity, direction, coordinate);
        this.positionType = positionType;
    }

    public static MoveEvent movingTo(Player player,
                                     Direction direction) {
        Optional<FootKungFu> footMagic = player.footKungFu();
        PositionType type = footMagic.map(magic -> magic.canFly() ? PositionType.FLY : PositionType.RUN)
                .orElse(PositionType.MOVE);
        return new MoveEvent(player, direction, player.coordinate(), type);
    }

    @Override
    protected PositionType getType() {
        return positionType;
    }


    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }

    public static MoveEvent movingTo(PassiveMonster monster, Direction direction) {
        return new MoveEvent(monster, direction, monster.coordinate(), PositionType.MOVE);
    }

    public static MoveEvent setPosition(PassiveMonster monster) {
        return new MoveEvent(monster, monster.direction(), monster.coordinate(), PositionType.SET);
    }

}
