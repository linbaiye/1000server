package org.y1000.entities.players;

import org.y1000.message.I2ClientMessage;

public interface Interpolation extends I2ClientMessage {
    State state();

    boolean canMerge(Interpolation interpolation);

    void merge(Interpolation interpolation);
}
