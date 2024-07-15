package org.y1000.message;

import lombok.AccessLevel;
import lombok.Setter;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerInfoPacket;
import org.y1000.network.gen.PlayerInterpolationPacket;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;

public final class PlayerInterpolation extends AbstractCreatureInterpolation {

    @Setter(AccessLevel.PRIVATE)
    private PlayerInfoPacket infoPacket;

    private Packet packet;


    private PlayerInterpolation(long id, Coordinate coordinate, State state, Direction direction, int elapsedMillis) {
        super(id, coordinate, state, direction, elapsedMillis);
    }

    @Override
    public Packet toPacket() {
        if (packet != null) {
            return packet;
        }
        packet = Packet.newBuilder()
                .setPlayerInterpolation(PlayerInterpolationPacket.newBuilder()
                        .setInterpolation(interpolationPacket())
                        .setInfo(infoPacket).build())
                .build();
        return packet;
    }

    public static PlayerInterpolation FromPlayer(PlayerImpl player, int elapsedMillis) {
        PlayerInterpolation playerInterpolation = new PlayerInterpolation(player.id(), player.coordinate(),
                player.stateEnum(), player.direction(),
                elapsedMillis);
        playerInterpolation.setInfoPacket(PlayerInfo.toPacket(player));
        return playerInterpolation;
    }
}
