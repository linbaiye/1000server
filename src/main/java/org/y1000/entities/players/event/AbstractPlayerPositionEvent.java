package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerSetPositionPacket;
import org.y1000.network.gen.PositionPacket;

public abstract class AbstractPlayerPositionEvent extends AbstractClientMessageEvent{

    public AbstractPlayerPositionEvent(Player player) {
        super(player, buildPacket(player));
    }

    private static Packet buildPacket(Player player) {
        PlayerSetPositionPacket.Builder builder = PlayerSetPositionPacket.newBuilder()
                .setPosition(
                        PositionPacket.newBuilder()
                                .setY(player.coordinate().y())
                                .setX(player.coordinate().x())
                                .setDirection(player.direction().value())
                                .setId(player.id()))
                .setState(player.stateEnum().value());
        return Packet.newBuilder()
                .setPlayerSetPosition(builder)
                .build();
    }
}
