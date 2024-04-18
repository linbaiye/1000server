package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.PassiveMonster;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.magic.FootMagic;
import org.y1000.message.serverevent.EntityEventHandler;
import org.y1000.util.Coordinate;

import java.util.Optional;

public class MoveEvent extends AbstractPositionEvent {

    private final MovementType movementType;

    public MoveEvent(Entity entity, Direction direction, Coordinate coordinate, MovementType movementType) {
        super(entity, direction, coordinate);
        this.movementType = movementType;
    }

    public static MoveEvent movingTo(Player player,
                                     Direction direction) {
        Optional<FootMagic> footMagic = player.footMagic();
        MovementType type = footMagic.map(magic -> magic.canFly() ? MovementType.FLY : MovementType.RUN)
                .orElse(MovementType.MOVE);
        return new MoveEvent(player, direction, player.coordinate(), type);
    }

    @Override
    protected MovementType getType() {
        return movementType;
    }

    @Override
    public void accept(EntityEventHandler visitor) {
        visitor.handle(this);
    }

    public static MoveEvent movingTo(PassiveMonster monster, Direction direction) {
        return new MoveEvent(monster, direction, monster.coordinate(), MovementType.MOVE);
    }

    public static MoveEvent setPosition(PassiveMonster monster) {
        return new MoveEvent(monster, monster.direction(), monster.coordinate(), MovementType.SET);
    }

}
