package org.y1000.entities.players.event;

import org.y1000.entities.players.Player;
import org.y1000.message.serverevent.PlayerEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.TeleportPacket;
import org.y1000.realm.Realm;
import org.y1000.util.Coordinate;

public class PlayerTeleportEvent extends AbstractPlayerEvent {

    private final Packet packet;

    public PlayerTeleportEvent(Player source, Realm realm, Coordinate coordinate) {
        super(source);
        packet = Packet.newBuilder().setTeleport(teleportPacket(realm, coordinate)).build();
    }

    public static TeleportPacket teleportPacket(Realm realm, Coordinate coordinate) {
        return TeleportPacket.newBuilder()
                .setMap(realm.map().mapFile())
                .setX(coordinate.x())
                .setY(coordinate.y())
                .setRealm(realm.name())
                .setBgm(realm.bgm())
                .setTile(realm.map().tileFile())
                .setObj(realm.map().objectFile())
                .setRof(realm.map().roofFile())
                .build();
    }

    @Override
    public void accept(PlayerEventVisitor playerEventHandler) {

    }

    @Override
    protected Packet buildPacket() {
        return packet;
    }
}
