package org.y1000.message.clientevent;

import org.apache.commons.lang3.NotImplementedException;

public interface ClientEvent {
    default long getPlayerId() {
        throw new NotImplementedException();
    }

    default void setPlayerId(long playerId) {
    }
}
