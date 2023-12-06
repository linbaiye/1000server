package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.message.*;
import org.y1000.message.input.InputMessage;
import org.y1000.message.input.RightMouseClick;
import org.y1000.message.input.RightMouseRelease;
import org.y1000.util.Coordinate;

import java.util.Optional;

@Slf4j
final class PlayerWalkState implements PlayerState {

    static final long MILLIS_PER_UNIT = 900;

    private long elapsedMilli;

    private InputMessage currentInput;

    private InputMessage lastReceivedInput;

    public PlayerWalkState(InputMessage trigger) {
        elapsedMilli = 0;
        currentInput = trigger;
    }

    @Override
    public Optional<I2ClientMessage> onRightMouseClicked(PlayerImpl player, RightMouseClick click) {
        if (currentInput != null) {
            lastReceivedInput = click;
            return Optional.empty();
        }
        handleRightClick(player, click);
        if (player.state() == State.IDLE) {
            return Optional.of(UpdateMovementStateMessage.fromPlayer(player, click.sequence()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<I2ClientMessage> onRightMouseReleased(PlayerImpl player, RightMouseRelease release) {
        if (currentInput != null) {
            lastReceivedInput = release;
            return Optional.empty();
        } else {
            player.changeState(PlayerIdleState.INSTANCE);
            return Optional.of(UpdateMovementStateMessage.fromPlayer(player, release.sequence()));
        }
    }

    @Override
    public State getState() {
        return State.WALK;
    }


    private void handleRightClick(PlayerImpl player, RightMouseClick click) {
        player.changeDirection(click.direction());
        Coordinate next = player.coordinate().moveBy(click.direction());
        if (!player.getRealm().canMoveTo(next)) {
            log.debug("{} not movable, changing back to idle.", next);
            player.changeState(PlayerIdleState.INSTANCE);
        }
        player.changeState(new PlayerWalkState(click));
    }


    @Override
    public Optional<I2ClientMessage> update(PlayerImpl player, long deltaMillis) {
        if (currentInput == null) {
            return Optional.empty();
        }
        elapsedMilli += deltaMillis;
        if (elapsedMilli < MILLIS_PER_UNIT) {
            return Optional.empty();
        }
        Coordinate newCoordinate = player.coordinate().moveBy(player.direction());
        log.debug("Moving to coordinate {}.", newCoordinate);
        player.changeCoordinate(newCoordinate);
        if (lastReceivedInput instanceof RightMouseRelease) {
            player.changeState(PlayerIdleState.INSTANCE);
        } else if (lastReceivedInput instanceof RightMouseClick click) {
            handleRightClick(player, click);
        }
        UpdateMovementStateMessage message = UpdateMovementStateMessage.fromPlayer(player, currentInput.sequence());
        currentInput = null;
        return Optional.of(message);
    }
}
