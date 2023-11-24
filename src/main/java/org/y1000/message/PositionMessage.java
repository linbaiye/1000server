package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.util.Coordinate;

public record PositionMessage(long sourceId, Coordinate coordinate, Direction direction, long timestamp) implements Message {

    @Override
    public MessageType type() {
        return MessageType.POSITION;
    }

    public static PositionMessage fromCreature(Creature creature) {
        return new PositionMessage(creature.id(), creature.coordinate(), creature.direction(), System.currentTimeMillis());
    }
}
