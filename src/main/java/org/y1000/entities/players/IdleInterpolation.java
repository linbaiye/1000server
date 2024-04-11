package org.y1000.entities.players;

import lombok.Builder;
import org.y1000.connection.gen.*;
import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

@Builder
public class IdleInterpolation implements Interpolation {

    private final long stateStartAtMillis;

    private short length;

    private final Coordinate coordinate;

    private final long id;

    private final Direction direction;

    private final long interpolationStart;

    @Override
    public long id() {
        return id;
    }

    @Override
    public State state() {
        return State.IDLE;
    }

    @Override
    public Coordinate coordinate() {
        return null;
    }

    @Override
    public Direction direction() {
        return null;
    }


    @Override
    public InterpolationPacket toPacket() {
        return InterpolationPacket.newBuilder()
                .setState(state().value())
                .setDirection(direction.value())
                .setX(coordinate.x())
                .setY(coordinate.y())
                .build();
    }
}
