package org.y1000.message;

import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.State;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PositionPacket;
import org.y1000.entities.Direction;
import org.y1000.entities.PhysicalEntity;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.util.Coordinate;

public abstract class AbstractPositionEvent implements EntityEvent, ServerMessage {

    private final long id;

    private final Direction direction;

    private final Coordinate coordinate;

    private final Creature source;

    private final State state;

    private Packet packet;


    public AbstractPositionEvent(Creature source, Direction direction, Coordinate coordinate, State state) {
        this.id = source.id();
        this.direction = direction;
        this.coordinate = coordinate;
        this.source = source;
        this.state = state;
    }


    @Override
    public PhysicalEntity source() {
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
            packet = Packet.newBuilder()
                    .setPositionPacket(PositionPacket.newBuilder()
                            .setState(state.value())
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
    public String toString() {
        return "AbstractPositionMessage{" +
                "id=" + id +
                ", direction=" + direction +
                ", coordinate=" + coordinate +
                ", type=" + getType().name() +
                ", state=" + state +
                '}';
    }
}
