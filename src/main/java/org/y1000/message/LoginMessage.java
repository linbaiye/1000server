package org.y1000.message;

import org.y1000.connection.gen.LoginPacket;
import org.y1000.connection.gen.Packet;
import org.y1000.entities.players.Player;
import org.y1000.util.Coordinate;

public record LoginMessage(Coordinate coordinate) implements I2ClientMessage {

    public static LoginMessage ofPlayer(Player player) {
        return new LoginMessage(player.coordinate());
    }

    @Override
    public Packet toPacket() {
        return Packet.newBuilder().setLoginPacket(
                LoginPacket.newBuilder()
                        .setX(coordinate().x())
                        .setY(coordinate().y())
                        .build()
        ).build();
    }
}
