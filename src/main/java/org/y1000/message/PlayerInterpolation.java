package org.y1000.message;

import lombok.AccessLevel;
import lombok.Setter;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerInterpolationPacket;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;

public final class PlayerInterpolation extends AbstractInterpolation {

    private final boolean male;

    @Setter(AccessLevel.PRIVATE)
    private String name = "";

    private PlayerInterpolation(long id, Coordinate coordinate, State state, Direction direction, int elapsedMillis, boolean male) {
        super(id, coordinate, state, direction, elapsedMillis);
        this.male = male;
    }


    @Override
    public Packet toPacket() {
        return Packet.newBuilder()
                .setPlayerInterpolation(PlayerInterpolationPacket.newBuilder()
                        .setInterpolation(interpolationPacket())
                        .setId(getId())
                        .setName(name)
                        .setMale(male))
                .build();
    }

    public static PlayerInterpolation FromPlayer(PlayerImpl player, int elapsedMillis) {
        PlayerInterpolation playerInterpolation = new PlayerInterpolation(player.id(), player.coordinate(),
                player.stateEnum(), player.direction(),
                elapsedMillis, player.isMale());
        playerInterpolation.setName(player.name());
        return playerInterpolation;
    }
}
