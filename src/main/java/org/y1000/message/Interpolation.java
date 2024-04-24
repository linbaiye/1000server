package org.y1000.message;

import org.y1000.network.gen.InterpolationPacket;
import org.y1000.entities.Direction;
import org.y1000.entities.players.State;
import org.y1000.util.Coordinate;

public record Interpolation(Coordinate coordinate, State state, Direction direction, long elapsedMillis) {

    public InterpolationPacket ToPacket() {
        return InterpolationPacket.newBuilder()
                .setY(coordinate.y())
                .setX(coordinate.x())
                .setState(state.value())
                .setElapsedMillis(elapsedMillis)
                .setDirection(direction.value())
                .build();
    }

    @Override
    public String toString() {
        return "Interpolation{" +
                "coordinate=" + coordinate +
                ", state=" + state +
                ", direction=" + direction +
                ", elapsedMillis=" + elapsedMillis +
                '}';
    }
}
