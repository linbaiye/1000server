package org.y1000.entities.teleport;

import org.y1000.message.AbstractEntityInterpolation;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ShowTeleportPacket;

public final class TeleportInterpolation extends AbstractEntityInterpolation {
    private final Packet packet;
    public TeleportInterpolation(StaticTeleport teleport) {
        super(teleport.id(), teleport.coordinate());
        packet = Packet.newBuilder()
                .setShowTeleport(ShowTeleportPacket.newBuilder()
                        .setId(teleport.id())
                        .setShape(teleport.shape())
                        .setCoordinateX(teleport.coordinate().x())
                        .setCoordinateY(teleport.coordinate().y())
                        .setName(teleport.name())).build();
    }

    @Override
    public Packet toPacket() {
        return packet;
    }
}
