package org.y1000.message;

import org.y1000.entities.Direction;
import org.y1000.entities.players.Player;
import org.y1000.entities.players.State;
import org.y1000.util.Coordinate;

public record UpdateMovementStateMessage(long id, long sequence, long timestamp, Direction direction, Coordinate coordinate, State state) implements UpdateMovementMessage {

    public static UpdateMovementStateMessage fromPlayer(Player player, long sequence) {
        return new UpdateMovementStateMessage(player.id(), sequence, System.currentTimeMillis(), player.direction(), player.coordinate(), player.state());
    }
}
