package org.y1000.entities.players;

import org.y1000.connection.gen.InterpolationPacket;
import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

public interface Interpolation {
    long id();

    State state();

    short duration();

    Coordinate coordinate();

    Direction direction();

    InterpolationPacket toPacket();
}
