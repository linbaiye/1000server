package org.y1000.input;

import org.apache.commons.lang3.NotImplementedException;

@Deprecated
public interface ClientEvent {
    default long getPlayerId() {
        throw new NotImplementedException();
    }

    default void setPlayerId(long playerId) {
    }
}
