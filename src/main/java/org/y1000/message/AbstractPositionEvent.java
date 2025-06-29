package org.y1000.message;

import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.OldPlayerStateEnum;
import org.y1000.entities.players.MoveAction;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PositionPacket;
import org.y1000.entities.Direction;
import org.y1000.event.IEntityEvent;
import org.y1000.util.Coordinate;

public abstract class AbstractPositionEvent implements IEntityEvent, I2ClientMessage {

    private final long id;

    private final Direction direction;

    private final Coordinate coordinate;

    private final Creature source;

    private final OldPlayerStateEnum playerStateEnum;

    private final MoveAction moveAction;

    private Packet packet;


    public AbstractPositionEvent(Creature source, Direction direction, Coordinate coordinate, OldPlayerStateEnum playerStateEnum) {
        this(source, direction, coordinate, playerStateEnum, null);
    }

    public AbstractPositionEvent(Creature source, Direction direction, Coordinate coordinate, OldPlayerStateEnum playerStateEnum, MoveAction moveAction) {
        this.id = source.id();
        this.direction = direction;
        this.coordinate = coordinate;
        this.source = source;
        this.playerStateEnum = playerStateEnum;
        this.moveAction = moveAction;
    }


    @Override
    public AttackableActiveEntity source() {
        return this.source;
    }

    protected Direction direction() {
        return direction;
    }

    protected Coordinate coordinate() {
        return coordinate;
    }

    protected abstract PositionType getType();


    @Override
    public Packet toPacket() {
        if (packet == null) {
            PositionPacket.Builder builder = PositionPacket.newBuilder()
                    .setState(playerStateEnum.value())
                    .setType(getType().value())
                    .setY(coordinate.y())
                    .setX(coordinate.x())
                    .setDirection(direction.value())
                    .setId(id);
            if (moveAction != null)
                builder.setMoveAction(moveAction.value());
            packet = Packet.newBuilder()
                    .setPositionPacket(builder.build())
                    .build();
        }
        return packet;
    }


    @Override
    public String toString() {
        return "AbstractPositionMessage{" +
                "id=" + id +
                ", direction=" + direction +
                ", coordinate=" + coordinate +
                ", type=" + getType().name() +
                ", state=" + playerStateEnum +
                '}';
    }
}
