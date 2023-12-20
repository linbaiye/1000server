package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.message.*;
import org.y1000.message.input.InputMessage;
import org.y1000.message.input.RightMouseClick;
import org.y1000.message.input.RightMouseRelease;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
final class PlayerWalkState implements PlayerState {

    static final long MILLIS_PER_UNIT = 900;

    private long elapsedMilli;

    private final InputMessage currentInput;

    private InputMessage lastReceivedInput;

    private boolean finished;

    public PlayerWalkState(InputMessage trigger) {
        elapsedMilli = 0;
        currentInput = trigger;
        finished = false;
    }

    @Override
    public List<I2ClientMessage> onRightMouseClicked(PlayerImpl player, RightMouseClick click) {
        if (!finished) {
            lastReceivedInput = click;
            return Collections.emptyList();
        }
        handleRightClick(player, click);
        return Collections.singletonList(UpdateMovementStateMessage.fromPlayer(player, currentInput.sequence()));
    }

    @Override
    public List<I2ClientMessage> onRightMouseReleased(PlayerImpl player, RightMouseRelease release) {
        if (!finished) {
            lastReceivedInput = release;
            return Collections.emptyList();
        } else {
            player.changeState(PlayerIdleState.INSTANCE);
            return Collections.singletonList(UpdateMovementStateMessage.fromPlayer(player, release.sequence()));
        }
    }

    @Override
    public State getState() {
        return State.WALK;
    }


    private Optional<I2ClientMessage> handleRightClick(PlayerImpl player, RightMouseClick click) {
        player.changeDirection(click.direction());
        Coordinate next = player.coordinate().moveBy(click.direction());
        if (!player.getRealm().canMoveTo(next)) {
            log.debug("{} not movable, changing back to idle.", next);
            player.changeState(PlayerIdleState.INSTANCE);
            return Optional.of(UpdateMovementStateMessage.fromPlayer(player, click.sequence()));
        } else {
            player.changeState(new PlayerWalkState(click));
        }
        return Optional.empty();
    }


    @Override
    public List<I2ClientMessage> update(PlayerImpl player, long deltaMillis) {
        if (finished) {
            return Collections.emptyList();
        }
        elapsedMilli += deltaMillis;
        if (elapsedMilli < MILLIS_PER_UNIT) {
            return Collections.emptyList();
        }
        finished = true;
        Coordinate newCoordinate = player.coordinate().moveBy(player.direction());
        log.debug("Moving to coordinate {}.", newCoordinate);
        player.changeCoordinate(newCoordinate);
        List<I2ClientMessage> result = new ArrayList<>(2);
        result.add(UpdateMovementStateMessage.fromPlayer(player, currentInput.sequence()));
        if (lastReceivedInput == null) {
            log.debug("Have not received input after finishing walking.");
        } else if (lastReceivedInput instanceof RightMouseRelease) {
            player.changeState(PlayerIdleState.INSTANCE);
            result.add(UpdateMovementStateMessage.fromPlayer(player, lastReceivedInput.sequence()));
        } else if (lastReceivedInput instanceof RightMouseClick click) {
            handleRightClick(player, click).ifPresent(result::add);
        }
        return result;
    }


    @Override
    public Interpolation snapshot() {
        return null;
    }
}
