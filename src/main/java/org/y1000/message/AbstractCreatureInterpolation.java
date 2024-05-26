package org.y1000.message;

import org.y1000.network.gen.InterpolationPacket;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;

public abstract class AbstractCreatureInterpolation extends AbstractEntityInterpolation {

    private final State state;
    private final Direction direction;
    private final int elapsedMillis;
    private InterpolationPacket interpolationPacket;

    public AbstractCreatureInterpolation(long id, Coordinate coordinate, State state, Direction direction, int elapsedMillis) {
        super(id, coordinate);
        this.state = state;
        this.direction = direction;
        this.elapsedMillis = elapsedMillis;
    }


    InterpolationPacket interpolationPacket() {
        if (interpolationPacket == null) {
            interpolationPacket =InterpolationPacket.newBuilder()
                    .setY(coordinate().y())
                    .setX(coordinate().x())
                    .setState(state.value())
                    .setElapsedMillis(elapsedMillis)
                    .setDirection(direction.value())
                    .build();
        }
        return interpolationPacket;
    }
}
