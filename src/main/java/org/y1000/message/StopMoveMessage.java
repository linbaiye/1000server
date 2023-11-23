package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.util.Coordinate;

public record StopMoveMessage(long sourceId, Coordinate coordinate, Direction direction, long timestamp) implements Message {
    @Override
    public MessageType type() {
        return MessageType.STOP_MOVE;
    }

    public static StopMoveMessage fromCreature(Creature creature) {
        return new StopMoveMessage(creature.id(), creature.coordinate(), creature.direction(), System.currentTimeMillis());
    }
}
