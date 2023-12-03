package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.message.Message;
import org.y1000.message.MoveMessage;
import org.y1000.message.PositionMessage;
import org.y1000.message.StopMoveMessage;
import org.y1000.message.input.RightMouseClick;

import java.util.Optional;

@Slf4j
final class PlayerWalkState implements PlayerState {

    static final long MILLIS_PER_UNIT = 900;

    private long elapsedMilli;

    private Direction nextDirection;

    private boolean keepMoving;

    public PlayerWalkState() {
        keepMoving = true;
        elapsedMilli = 0;
    }

    @Override
    public Optional<Message> stopMove(PlayerImpl player, StopMoveMessage stopMoveMessage) {
        if (!keepMoving) {
            return Optional.empty();
        }
        keepMoving = false;
        return Optional.of(StopMoveMessage.fromCreature(player));
    }

    @Override
    public Optional<Message> sit(PlayerImpl player) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> move(PlayerImpl player, MoveMessage moveMessage) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> onRightMouseClicked(PlayerImpl player, RightMouseClick click) {
        if (player.direction() != click.direction() || !keepMoving) {
            keepMoving = true;
            nextDirection = click.direction();
        }
        return Optional.of(MoveMessage.fromPlayer(player, click.sequence()));
    }

    @Override
    public State getState() {
        return State.WALK;
    }

    @Override
    public Optional<Message> update(PlayerImpl player, long deltaMillis) {
        elapsedMilli += deltaMillis;
        if (elapsedMilli < MILLIS_PER_UNIT) {
            return Optional.empty();
        }
        player.changeCoordinate(player.coordinate().moveBy(player.direction()));
        if (keepMoving) {
            elapsedMilli -= MILLIS_PER_UNIT;
            if (nextDirection != null && nextDirection != player.direction()) {
                elapsedMilli = 0;
                player.changeDirection(nextDirection);
            }
            if (player.getRealm().canMoveTo(player.coordinate().moveBy(player.direction()))) {
                return Optional.of(MoveMessage.fromPlayer(player, 0));
            }
        }
        player.changeState(PlayerIdleState.INSTANCE);
        return Optional.of(PositionMessage.fromCreature(player));
    }
}
