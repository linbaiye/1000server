package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.util.Coordinate;

import java.util.Optional;

public record MoveMessage(Direction direction, Coordinate coordinate, long sourceId, long timestamp) implements Message {

    @Override
    public MessageType type() {
        return MessageType.MOVE;
    }

    public static MoveMessage fromCreature(Creature creature) {
        return new MoveMessage(creature.direction(), creature.coordinate(), creature.id(), System.currentTimeMillis());
    }

    @Override
    public Optional<Message> dispatch(MessageHandler handler) {
        return handler.handle(this);
    }
}
