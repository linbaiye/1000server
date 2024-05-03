package org.y1000.message.input;

import org.y1000.entities.Direction;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerMoveState;
import org.y1000.message.InputResponseMessage;
import org.y1000.message.MoveEvent;
import org.y1000.util.Coordinate;

public abstract class AbstractRightClick extends AbstractInput implements MoveInput {
    private final Direction direction;

    public AbstractRightClick( long sequence, Direction direction) {
        super(sequence);
        this.direction = direction;
    }

    public Direction direction() {
        return direction;
    }

    @Override
    public String toString() {
        return "Input{" +
                "sequence=" + sequence() +
                ", direction=" + direction +
                ", type=" + type().name() +
                '}';
    }

    @Override
    public void move(PlayerImpl player) {
        Coordinate coordinate = player.coordinate().moveBy(direction());
        player.changeDirection(direction());
        if (player.getRealm().map().movable(coordinate)) {
            player.changeState(PlayerMoveState.move(player, this));
            player.emitEvent(new InputResponseMessage(sequence(), MoveEvent.movingTo(player, direction())));
        } else {
            changeToIdle(player);
        }
    }
}
