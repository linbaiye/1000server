package org.y1000.message;

import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerInterpolationPacket;
import org.y1000.entities.Direction;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.State;
import org.y1000.util.Coordinate;

public class PlayerInterpolation extends AbstractInterpolation {

    private final boolean male;


    private PlayerInterpolation(long id, Coordinate coordinate, State state, Direction direction, long elapsedMillis, boolean male) {
        super(id, coordinate, state, direction, elapsedMillis);
        this.male = male;
    }


    @Override
    public Packet toPacket() {
        return Packet.newBuilder()
                .setPlayerInterpolation(PlayerInterpolationPacket.newBuilder()
                        .setInterpolation(interpolationPacket())
                        .setId(getId())
                        .setMale(male))
                .build();
    }


    public static PlayerInterpolation FromPlayer(Player player, long elapsedMillis) {
        return new PlayerInterpolation(player.id(), player.coordinate(),
                player.stateEnum(), player.direction(),
                elapsedMillis, player.isMale());
    }

}
