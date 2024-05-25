package org.y1000.entities.item;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.message.AbstractNamedCreatureInterpolation;
import org.y1000.network.gen.Packet;
import org.y1000.util.Coordinate;

public class GroundedItemInterpolation extends AbstractNamedCreatureInterpolation {
    public GroundedItemInterpolation(long id, Coordinate coordinate, State state, Direction direction, int elapsedMillis, String name) {
        super(id, coordinate, state, direction, elapsedMillis, name);
    }

    @Override
    public Packet toPacket() {
        return null;
    }
}
