package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.util.Coordinate;

public record PositionMessage(Direction direction, Coordinate coordinate, long sourceId, long timestamp) implements Message {

    @Override
    public MessageType type() {
        return MessageType.POSITION;
    }

    public static PositionMessage fromCreature(Creature creature) {
        return new PositionMessage(creature.direction(), creature.coordinate(), creature.id(), System.currentTimeMillis());
    }
}
