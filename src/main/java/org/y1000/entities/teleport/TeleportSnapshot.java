package org.y1000.entities.teleport;

import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ShowTeleportPacket;

public final class TeleportSnapshot implements I2ClientMessage {
    private final Packet packet;
    public TeleportSnapshot(StaticTeleport teleport) {
        packet = Packet.newBuilder()
                .setShowTeleport(ShowTeleportPacket.newBuilder()
                        .setId(teleport.id())
                        .setShape(teleport.shape())
                        .setCoordinateX(teleport.coordinate().x())
                        .setCoordinateY(teleport.coordinate().y())
                        .setName(teleport.viewName())).build();
    }

    @Override
    public Packet toPacket() {
        return packet;
    }
}
