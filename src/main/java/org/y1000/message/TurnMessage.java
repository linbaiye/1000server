package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.util.Coordinate;

public record TurnMessage(Direction newDirection, Coordinate coordinate, long sourceId, long timestamp) implements Message {

    @Override
    public MessageType type() {
        return MessageType.TURN;
    }

    public static TurnMessage fromCreature(Creature creature) {
        return new TurnMessage( creature.direction(), creature.coordinate(), creature.id(), System.currentTimeMillis());
    }
}
