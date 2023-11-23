package org.y1000.entities.creatures.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.message.Message;
import org.y1000.message.MoveMessage;
import org.y1000.message.PositionMessage;
import org.y1000.message.StopMoveMessage;

import java.util.Optional;

@Slf4j
final class PlayerWalkState implements PlayerState {

    private static final float MILLIS_PER_UNIT = 900f;
    private float elapsed;

    private Direction movingDirection;

    private boolean keepMoving;

    public PlayerWalkState() {
        keepMoving = true;
        elapsed = 0;
    }

    @Override
    public Optional<Message> sit(PlayerImpl player) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> stopMove(PlayerImpl player, StopMoveMessage stopMoveMessage) {
        keepMoving = false;
        return Optional.of(stopMoveMessage);
    }

    @Override
    public Optional<Message> move(PlayerImpl player, MoveMessage moveMessage) {
        if (player.direction() != moveMessage.direction() || !keepMoving) {
            keepMoving = true;
            movingDirection = moveMessage.direction();
            return Optional.of(moveMessage);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(PlayerImpl player, long deltaMillis) {
        if (!player.canMoveTo(player.coordinate().moveBy(player.direction()))) {
            return Optional.of(PositionMessage.fromCreature(player));
        }
        elapsed += deltaMillis;
        if (elapsed < MILLIS_PER_UNIT) {
            return Optional.empty();
        }
        player.changeCoordinate(player.coordinate().moveBy(player.direction()));
        if (keepMoving) {
            elapsed -= MILLIS_PER_UNIT;
            if (movingDirection != null && movingDirection != player.direction()) {
                elapsed = 0;
                player.changeDirection(movingDirection);
            }
            return Optional.of(MoveMessage.fromCreature(player));
        } else {
            player.changeState(PlayerIdleState.INSTANCE);
            return Optional.empty();
        }
    }
}
