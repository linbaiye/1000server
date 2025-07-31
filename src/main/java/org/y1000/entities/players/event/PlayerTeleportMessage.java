package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.TeleportPacket;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

public class PlayerTeleportMessage extends Abstract2PlayerMessageEvent {

    public PlayerTeleportMessage(Player source, Packet packet) {
        super(source, packet);
    }

    public static TeleportPacket teleportPacket(Realm realm, Coordinate coordinate) {
        return TeleportPacket.newBuilder()
                .setMap(realm.map().mapFile())
                .setX(coordinate.x())
                .setY(coordinate.y())
                .setTitle(realm.title())
                .setBgm(realm.bgm())
                .setResource(realm.map().resource())
                .build();
    }

    public static PlayerTeleportMessage of(Player player) {
        var pkt = teleportPacket(player.getRealm(), player.coordinate());
        return new PlayerTeleportMessage(player, Packet.newBuilder().setTeleport(pkt).build());
    }
}
