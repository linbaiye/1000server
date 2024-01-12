package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.message.*;
import org.y1000.message.input.InputMessage;
import org.y1000.message.input.RightMouseClick;
import org.y1000.message.input.RightMousePressedMotion;
import org.y1000.message.input.RightMouseRelease;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
final class PlayerWalkState implements PlayerState {

    static final long MILLIS_PER_UNIT = 900;

    // Milliseconds this state lasts.
    private long lastMillis;

    private InputMessage currentInput;

    private InputMessage lastReceivedInput;

    private long walkMillis ;

    public PlayerWalkState(InputMessage trigger) {
        lastMillis = 0;
        currentInput = trigger;
        walkMillis = 0;
    }

    @Override
    public List<I2ClientMessage> onRightMouseClicked(PlayerImpl player, RightMouseClick click) {
        lastReceivedInput = null;
        return Collections.emptyList();
    }

    @Override
    public List<I2ClientMessage> onRightMouseReleased(PlayerImpl player, RightMouseRelease release) {
        lastReceivedInput = null;
        return Collections.emptyList();
    }

    @Override
    public State getState() {
        return State.WALK;
    }


    @Override
    public List<I2ClientMessage> OnRightMousePressedMotion(PlayerImpl player,
                                                           RightMousePressedMotion motion) {
        lastReceivedInput = motion;
        return Collections.emptyList();
    }

    private List<I2ClientMessage> handleMotion(PlayerImpl player, RightMousePressedMotion motion) {
        player.changeDirection(motion.direction());
        Coordinate next = player.coordinate().moveBy(motion.direction());
        if (!player.getRealm().canMoveTo(next)) {
            log.debug("{} not movable, changing back to idle.", next);
            player.changeState(new PlayerIdleState());
            return Collections.singletonList(UpdateMovementStateMessage.fromPlayer(player, motion.sequence()));
        } else {
            walkMillis = 0;
            currentInput = motion;
            lastReceivedInput = null;
            return Collections.emptyList();
        }
    }


    @Override
    public List<I2ClientMessage> update(PlayerImpl player, long deltaMillis) {
        lastMillis += deltaMillis;
        walkMillis += deltaMillis;
        if (walkMillis < MILLIS_PER_UNIT) {
            return Collections.emptyList();
        }
        Coordinate newCoordinate = player.coordinate().moveBy(player.direction());
        log.debug("Moving to coordinate {}.", newCoordinate);
        player.changeCoordinate(newCoordinate);
        if (lastReceivedInput == null) {
            player.changeState(new PlayerWalkState(currentInput));
            return Collections.singletonList(UpdateMovementStateMessage.fromPlayer(player, currentInput.sequence()));
        } else if (lastReceivedInput instanceof RightMousePressedMotion motion) {
            return handleMotion(player, motion);
        } else {
            player.changeState(new PlayerIdleState());
            return Collections.singletonList(UpdateMovementStateMessage.fromPlayer(player, currentInput.sequence()));
        }
    }

    @Override
    public Interpolation captureInterpolation(PlayerImpl player, long stateStartedAtMillis) {
        return null;
    }
}
