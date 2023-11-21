package org.y1000.entities.creatures.players;

import org.y1000.entities.Direction;
import org.y1000.message.Message;
import org.y1000.message.MoveMessage;
import org.y1000.util.Coordinate;

import java.util.Optional;

public class PlayerWalkState implements PlayerState {

    //private final static Vector2 WALK_VELOCITY = new Vector2(32, 24);
    private static final float VELOCITY = 1 / 0.9f;

    private long beginAtMilli;

    private Coordinate beginAt;

    private Direction movingDirection;


    public PlayerWalkState(long beginAtMilli, Coordinate beginAt, Direction movingDirection) {
        this.beginAtMilli = beginAtMilli;
        this.beginAt = beginAt;
        this.movingDirection = movingDirection;
    }

    @Override
    public Message sit(Player player) {
        return null;
    }

    @Override
    public Message move(Player player, MoveMessage moveMessage) {
        return null;
    }

    @Override
    public Optional<Message> update(Player player, long delta) {
        double distance = ((double) (beginAtMilli + delta) / 1000 * VELOCITY);
        if (distance >= 1) {
            player.changeCoordinate(player.coordinate().moveBy(movingDirection));
        }
        return Optional.empty();
    }
}
