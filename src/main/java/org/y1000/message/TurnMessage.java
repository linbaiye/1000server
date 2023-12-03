package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.players.State;
import org.y1000.util.Coordinate;

public record TurnMessage(Direction direction, Coordinate coordinate, long sourceId, long timestamp, long sequence) implements UpdateMovementMessage {

    @Override
    public State state() {
        return State.IDLE;
    }

    public static TurnMessage fromCreature(Creature creature) {
        return new TurnMessage( creature.direction(), creature.coordinate(), creature.id(), System.currentTimeMillis(), 0);
    }
}
