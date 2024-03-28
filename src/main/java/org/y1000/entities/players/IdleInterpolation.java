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
    public short duration() {
        return length;
    }

    @Override
    public long stateStartAtMillis() {
        return stateStartAtMillis;
    }

    @Override
    public long interpolationStartAtMillis() {
        return interpolationStart;
    }

    @Override
    public InterpolationPacket toPacket() {
        return InterpolationPacket.newBuilder()
                .setDuration(duration())
                .setState(state().value())
                .setDirection(direction.value())
                .setX(coordinate.x())
                .setY(coordinate.y())
                .setId(id())
                .setStateStart(stateStartAtMillis())
                .setInterpolationStart(interpolationStartAtMillis())
                .build();
    }
}
