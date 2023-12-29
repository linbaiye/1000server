package org.y1000.entities.players;

import org.y1000.connection.gen.InterpolationPacket;
import org.y1000.message.Message;

public interface Interpolation extends Message {
    long id();

    State state();

    short duration();

    long stateStartAtMillis();

    long interpolationStartAtMillis();

    InterpolationPacket toPacket();
}
