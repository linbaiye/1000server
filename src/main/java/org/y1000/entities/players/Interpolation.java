package org.y1000.entities.players;

import org.y1000.connection.gen.InterpolationPacket;

public interface Interpolation {
    long id();

    State state();

    short duration();

    long stateStartAtMillis();

    long interpolationStartAtMillis();

    InterpolationPacket toPacket();
}
