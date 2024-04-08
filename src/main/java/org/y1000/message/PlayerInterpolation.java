package org.y1000.message;

import org.y1000.connection.gen.Packet;
import org.y1000.connection.gen.PlayerInterpolationPacket;

public class PlayerInterpolation implements ServerEvent {

    private final Interpolation interpolation;

    private final boolean male;

    public PlayerInterpolation(Interpolation interpolation, boolean male) {
        this.interpolation = interpolation;
        this.male = male;
    }

    @Override
    public Packet toPacket() {
        return Packet.newBuilder()
                .setPlayerInterpolation(PlayerInterpolationPacket.newBuilder()
                        .setInterpolation(interpolation.ToPacket())
                        .setMale(male))
                .build();
    }
}
