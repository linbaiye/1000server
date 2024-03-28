package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.message.*;
import org.y1000.message.input.InputMessage;
import org.y1000.message.input.RightMouseClick;
import org.y1000.message.input.RightMousePressedMotion;
import org.y1000.message.input.RightMouseRelease;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
final class PlayerWalkState implements PlayerState {

    private static final long MILLIS_TO_WALK_ONE_UNIT = 900;

    private InputMessage currentInput;

    private InputMessage lastReceivedInput;

    private long walkedMillis;

    public PlayerWalkState(InputMessage trigger) {
        currentInput = trigger;
        walkedMillis = 0;
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



    private Optional<I2ClientMessage> handleNonReleaseInput(PlayerImpl player, InputMessage trigger, Direction direction) {
        player.changeDirection(direction);
        Coordinate next = player.coordinate().moveBy(direction);
        if (!player.getRealm().canMoveTo(next)) {
            log.debug("{} not movable, changing back to idle.", next);
            player.changeState(new PlayerIdleState());
            return UpdateMovementStateMessage.fromPlayer(player, trigger.sequence());
        } else {
            player.changeState(new PlayerWalkState(trigger));
            return null;
        }
    }



    @Override
    public List<I2ClientMessage> update(PlayerImpl player, long deltaMillis) {
        walkedMillis += deltaMillis;
        if (walkedMillis < MILLIS_TO_WALK_ONE_UNIT) {
            return Collections.emptyList();
        }
        List<I2ClientMessage> messages = new ArrayList<>();
        Coordinate newCoordinate = player.coordinate().moveBy(player.direction());
        log.debug("Moving to coordinate {}.", newCoordinate);
        player.changeCoordinate(newCoordinate);
        messages.add(UpdateMovementStateMessage.fromPlayer(player, currentInput.sequence()));
        if (lastReceivedInput == null) {
            messages.add(handleNonReleaseInput(player, currentInput, player.direction()));
        } else if (lastReceivedInput instanceof RightMousePressedMotion motion) {
            return handleNonReleaseInput(player, motion, motion.direction());
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
