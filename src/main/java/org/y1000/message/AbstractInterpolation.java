package org.y1000.message;

import org.y1000.network.gen.InterpolationPacket;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;

public abstract class AbstractInterpolation implements ServerMessage {

    private final long id;
    private final Coordinate coordinate;
    private final State state;
    private final Direction direction;
    private final long elapsedMillis;
    private InterpolationPacket interpolationPacket;

    public AbstractInterpolation(long id, Coordinate coordinate, State state, Direction direction, long elapsedMillis) {
        this.id = id;
        this.coordinate = coordinate;
        this.state = state;
        this.direction = direction;
        this.elapsedMillis = elapsedMillis;
    }

    long getId() {
        return id;
    }

    InterpolationPacket interpolationPacket() {
        if (interpolationPacket == null) {
            interpolationPacket =InterpolationPacket.newBuilder()
                    .setY(coordinate.y())
                    .setX(coordinate.x())
                    .setState(state.value())
                    .setElapsedMillis(elapsedMillis)
                    .setDirection(direction.value())
                    .build();
        }
        return interpolationPacket;
    }
}
