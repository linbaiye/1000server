package org.y1000.message;

import org.y1000.connection.gen.LoginPacket;
import org.y1000.connection.gen.Packet;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

public record LoginMessage(long id, Coordinate coordinate) implements I2ClientMessage {

    @Override
    public Packet toPacket() {
        return Packet.newBuilder().setLoginPacket(
                LoginPacket.newBuilder()
                        .setX(coordinate().x())
                        .setY(coordinate().y())
                        .setId(id())
                        .build()
        ).build();
    }

    @Override
    public long timestamp() {
        return 0;
    }
}
