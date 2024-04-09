package org.y1000.message;

import org.y1000.connection.gen.Packet;
import org.y1000.connection.gen.PositionPacket;
import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

import java.util.Optional;

public abstract class AbstractPositionMessage implements CreatureMessage {

    private final long id;

    private final Direction direction;

    private final Coordinate coordinate;

    private Packet packet;

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
        if (packet == null) {
            packet = Packet.newBuilder()
                    .setPositionPacket(PositionPacket.newBuilder()
                            .setType(getType().value())
                            .setY(coordinate.y())
                            .setX(coordinate.x())
                            .setDirection(direction.value())
                            .setId(id)
                            .build())
                    .build();
        }
        return packet;
    }

    @Override
    public Optional<ServerEvent> eventToPlayer(long id) {
        return Optional.of(this);
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
