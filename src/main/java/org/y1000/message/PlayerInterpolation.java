package org.y1000.message;

import org.y1000.connection.gen.Packet;
import org.y1000.connection.gen.PlayerInterpolationPacket;
import org.y1000.entities.Entity;

public class PlayerInterpolation implements ServerMessage {

    private final Interpolation interpolation;

    private final boolean male;

    public PlayerInterpolation(Interpolation interpolation, boolean male) {
        this.interpolation = interpolation;
        this.male = male;
    }

    @Override
    public String toString() {
        return "PlayerInterpolation{" +
                "interpolation=" + interpolation +
                ", male=" + male +
                '}';
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
