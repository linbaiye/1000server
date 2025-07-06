package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerSetPositionPacket;
import org.y1000.network.gen.PositionPacket;
import org.y1000.realm.PlayerEventHandler;

public final class PlayerSetPositionEvent extends AbstractClientMessageEvent {

    public PlayerSetPositionEvent(Player player, Packet packet) {
        super(player, packet);
    }

    public static PlayerSetPositionEvent of(Player player) {
        PlayerSetPositionPacket.Builder builder = PlayerSetPositionPacket.newBuilder()
                .setPosition(
                        PositionPacket.newBuilder()
                                .setY(player.coordinate().y())
                                .setX(player.coordinate().x())
                                .setDirection(player.direction().value())
                                .setId(player.id()))
                .setState(player.state().playerStateEnum().value());
        var packet = Packet.newBuilder()
                .setPlayerSetPosition(builder)
                .build();
        return new PlayerSetPositionEvent(player, packet);
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        handler.onMoved(source());
        handler.sendTo(source(), this);
    }
}
