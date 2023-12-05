package org.y1000.message;

import org.y1000.connection.gen.MovementPacket;
import org.y1000.connection.gen.Packet;
import org.y1000.entities.Direction;
import org.y1000.entities.players.State;
import org.y1000.util.Coordinate;

public interface UpdateMovementMessage extends UpdateStateMessage {
    Direction direction();

    Coordinate coordinate();

    State state();

    long sequence();

    default Packet toPacket() {
        return Packet.newBuilder().setMovementPacket(
                        MovementPacket.newBuilder()
                                .setState(state().value())
                                .setDirection(direction().value())
                                .setX(coordinate().x())
                                .setY(coordinate().y())
                                .setSequence(sequence())
                                .setTimestamp(timestamp())
                                .setId((int) id())
                        .build()
                ).build();
    }
}
