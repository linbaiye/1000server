package org.y1000.message;

import org.y1000.connection.gen.Packet;

import java.util.Optional;

public interface ServerEvent extends ServerMessage {

    void accept(ServerEventVisitor visitor);

    default Optional<ServerEvent> eventToPlayer(long id) {
        return Optional.empty();
    }
}
