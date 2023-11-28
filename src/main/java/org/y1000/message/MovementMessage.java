package org.y1000.message;

import org.y1000.connection.gen.MovementPacket;
import org.y1000.connection.gen.Packet;
import org.y1000.entities.Direction;
import org.y1000.util.Coordinate;

public interface MovementMessage extends Message {
    Direction direction();

    Coordinate coordinate();


    default Packet toPacket() {
        return Packet.newBuilder().setMovementPacket(
                        MovementPacket.newBuilder()
                                .setType(type().value())
                                .setDirection(direction().value())
                                .setX(coordinate().x())
                                .setY(coordinate().y())
                                .setTimestamp(timestamp())
                                .setId((int)sourceId())
                        .build()
                ).build();
    }
}
