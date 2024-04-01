package org.y1000.message;

import org.y1000.connection.gen.Packet;
import org.y1000.connection.gen.PositionPacket;
import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

public abstract class AbstractPositionMessage implements CreatureMessage {

    private final long id;

    private final Direction direction;

    private final Coordinate coordinate;

    public AbstractPositionMessage(long id, Direction direction, Coordinate coordinate) {
        this.id = id;
        this.direction = direction;
        this.coordinate = coordinate;
    }

    @Override
    public long id() {
        return id;
    }

    protected abstract PositionType getType();

    @Override
    public Packet toPacket() {
        return Packet.newBuilder()
                .setPositionPacket(PositionPacket.newBuilder()
                        .setType(getType().value())
                        .setY(coordinate.y())
                        .setX(coordinate.x())
                        .setDirection(direction.value())
                        .setId(id)
                        .build())
                .build();
    }

    @Override
    public String toString() {
        return "AbstractPositionMessage{" +
                "id=" + id +
                ", direction=" + direction +
                ", coordinate=" + coordinate +
                ", type=" + getType().name() +
                '}';
    }
}
