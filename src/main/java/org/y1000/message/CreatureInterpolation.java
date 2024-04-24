package org.y1000.message;

import org.y1000.network.gen.CreatureInterpolationPacket;
import org.y1000.network.gen.Packet;
import org.y1000.entities.Direction;
import org.y1000.entities.players.State;
import org.y1000.util.Coordinate;

public final class CreatureInterpolation extends AbstractNamedInterpolation {

    public CreatureInterpolation(long id, Coordinate coordinate, State state,
                                 Direction direction, long elapsedMillis, String name) {
        super(id, coordinate, state, direction, elapsedMillis, name);
    }

    @Override
    public Packet toPacket() {
        return Packet.newBuilder().setCreatureInterpolation(
                CreatureInterpolationPacket.newBuilder()
                        .setInterpolation(interpolationPacket())
                        .setId(getId())
                        .setName(getName())
                        .build()
        ).build();
    }
}
