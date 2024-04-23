package org.y1000.message;

import org.y1000.connection.gen.LoginPacket;
import org.y1000.connection.gen.Packet;
import org.y1000.entities.Entity;
import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.message.serverevent.EntityEventHandler;
import org.y1000.message.serverevent.PlayerEventHandler;
import org.y1000.util.Coordinate;

public record LoginSucceededEvent(Player player, Coordinate coordinate) implements EntityEvent {

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
    public void accept(EntityEventHandler visitor) {
        if (visitor instanceof PlayerEventHandler playerEventHandler) {
            playerEventHandler.handle(this);
        }
    }

    @Override
    public Entity source() {
        return player;
    }
}
