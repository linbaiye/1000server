package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;

public record TurnMessage(long sourceId, Direction newDirection, long timestamp) implements Message {

    @Override
    public MessageType type() {
        return MessageType.TURN;
    }

    public static TurnMessage fromCreature(Creature creature) {
        return new TurnMessage(creature.id(), creature.direction(), System.currentTimeMillis());
    }
}
