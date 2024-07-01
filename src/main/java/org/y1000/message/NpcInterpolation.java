package org.y1000.message;

import lombok.Builder;
import org.y1000.entities.creatures.NpcType;
import org.y1000.network.gen.CreatureInterpolationPacket;
import org.y1000.network.gen.Packet;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;

public final class NpcInterpolation extends AbstractNamedCreatureInterpolation {
    private final NpcType type;

    @Builder
    public NpcInterpolation(long id, Coordinate coordinate, State state,
                            Direction direction, int elapsedMillis, String name, NpcType type) {
        super(id, coordinate, state, direction, elapsedMillis, name);
        this.type = type;
    }

    @Override
    public Packet toPacket() {
        return Packet.newBuilder().setCreatureInterpolation(
                CreatureInterpolationPacket.newBuilder()
                        .setInterpolation(interpolationPacket())
                        .setId(getId())
                        .setName(getName())
                        .setType(type.value())
                        .build()
        ).build();
    }
}
