package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.FollowRopePacket;
import org.y1000.network.gen.Packet;

public class PlayerFollowRopeEvent extends Abstract2VisibleAndSelfMessageEvent {

    public PlayerFollowRopeEvent(Player player, Packet packet) {
        super(player, packet);
    }

    public static PlayerFollowRopeEvent turn(Player player) {
        FollowRopePacket packet = FollowRopePacket.newBuilder()
                .setDirection(player.direction().value())
                .setX(player.coordinate().x())
                .setX(player.coordinate().y())
                .setDurationMillis(0)
                .setId(player.id()).build();
        return new PlayerFollowRopeEvent(player, Packet.newBuilder().setFollowRope(packet).build());
    }

    public static PlayerFollowRopeEvent follow(Player player) {
        FollowRopePacket packet = FollowRopePacket.newBuilder()
                .setDirection(player.direction().value())
                .setX(player.coordinate().x())
                .setX(player.coordinate().y())
                .setDurationMillis(200)
                .setId(player.id()).build();
        return new PlayerFollowRopeEvent(player, Packet.newBuilder().setFollowRope(packet).build());
    }

}
