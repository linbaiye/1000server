package org.y1000.message;

import lombok.Getter;
import org.y1000.connection.gen.InterpolationPacket;
import org.y1000.entities.Direction;
import org.y1000.entities.players.State;
import org.y1000.util.Coordinate;

@Getter
public record Interpolation(Coordinate coordinate, State state, Direction direction, long elapsedMillis, long id) {

    public InterpolationPacket ToPacket()
    {
        return InterpolationPacket.newBuilder()
                .setY(coordinate.y())
                .setX(coordinate.x())
                .setId(id)
                .setState(state.value())
                .setElapsedMillis(elapsedMillis)
                .setDirection(direction.value())
                .build();
    }

}
