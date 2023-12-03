package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.players.State;
import org.y1000.util.Coordinate;

public record StopMoveMessage(Direction direction, Coordinate coordinate, long sourceId, long timestamp, long sequence) implements UpdateMovementMessage {
    @Override
    public State state() {
        return State.IDLE;
    }

    public static StopMoveMessage fromCreature(Creature creature) {
        return new StopMoveMessage(creature.direction(), creature.coordinate(), creature.id(), System.currentTimeMillis(), 0);
    }
}
