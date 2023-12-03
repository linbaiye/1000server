package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.State;
import org.y1000.util.Coordinate;


public record MoveMessage(Direction direction, Coordinate coordinate, long sourceId, long timestamp, long sequence, State state) implements UpdateMovementMessage {

    @Override
    public State state() {
        return State.WALK;
    }


    public static MoveMessage fromPlayer(Player player, long sequence) {
        return new MoveMessage(player.direction(), player.coordinate(), player.id(), System.currentTimeMillis(), sequence, player.state());
    }

    @Override
    public String toString() {
        return "MoveMessage{" +
                "direction=" + direction +
                ", coordinate=" + coordinate +
                ", sourceId=" + sourceId +
                ", timestamp=" + timestamp +
                ", sequence=" + sequence +
                ", state=" + state +
                '}';
    }
}
