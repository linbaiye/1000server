package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.y1000.message.*;
import org.y1000.message.input.RightMouseClick;
import org.y1000.message.input.RightMouseRelease;

import java.util.Optional;

@Slf4j
final class PlayerIdleState implements PlayerState {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerState.class);

    public static final PlayerIdleState INSTANCE = new PlayerIdleState();

    public PlayerIdleState() {
    }


    @Override
    public Optional<I2ClientMessage> onRightMouseClicked(PlayerImpl player, RightMouseClick click) {
        var nextCoordinate = player.coordinate().moveBy(click.direction());
        player.changeDirection(click.direction());
        if (!player.getRealm().canMoveTo(nextCoordinate)) {
            return Optional.of(UpdateMovementStateMessage.fromPlayer(player, click.sequence()));
        }
        player.changeState(new PlayerWalkState(click));
        return Optional.empty();
    }

    @Override
    public Optional<I2ClientMessage> onRightMouseReleased(PlayerImpl player, RightMouseRelease release) {
        return Optional.empty();
    }

    @Override
    public State getState() {
        return State.IDLE;
    }

    @Override
    public Optional<I2ClientMessage> update(PlayerImpl player, long deltaMillis) {
        return Optional.empty();
    }
}
