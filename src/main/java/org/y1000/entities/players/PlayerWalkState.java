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

    private final InputMessage currentInput;

    private InputMessage lastReceivedInput;

    private long walkedMillis;

    public PlayerWalkState(InputMessage trigger) {
        currentInput = trigger;
        walkedMillis = 0;
    }

    @Override
    public List<I2ClientMessage> onRightMouseClicked(PlayerImpl player, RightMouseClick click) {
        lastReceivedInput = click;
        return Collections.emptyList();
    }

    @Override
    public List<I2ClientMessage> onRightMouseReleased(PlayerImpl player, RightMouseRelease release) {
        lastReceivedInput = release;
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


    @Override
    public List<I2ClientMessage> update(PlayerImpl player, long deltaMillis) {
        walkedMillis += deltaMillis;
        if (walkedMillis < MILLIS_TO_WALK_ONE_UNIT) {
            return Collections.emptyList();
        }
        Coordinate newCoordinate = player.coordinate().moveBy(player.direction());
        player.changeCoordinate(newCoordinate);
        if (lastReceivedInput == null) {
            return Collections.singletonList(Mover.moveOrIdle(player, currentInput, player.direction()));
        } else if (lastReceivedInput instanceof RightMousePressedMotion motion) {
            return Collections.singletonList(Mover.moveOrIdle(player, motion, motion.direction()));
        } else if (lastReceivedInput instanceof RightMouseRelease) {
            player.changeState(new PlayerIdleState());
            return Collections.singletonList(new InputResponseMessage(lastReceivedInput.sequence(), SetPositionMessage.fromPlayer(player)));
        }
        return Collections.emptyList();
    }

    @Override
    public Interpolation captureInterpolation(PlayerImpl player, long stateStartedAtMillis) {
        return null;
    }
}
