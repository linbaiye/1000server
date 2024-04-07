package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.message.*;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.input.*;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
final class PlayerWalkState implements PlayerState {

    private static final long MILLIS_TO_WALK_ONE_UNIT = 900;

    private final AbstractRightClick currentInput;

    private long walkedMillis;

    private CharacterMovementEvent lastReceived;

    public PlayerWalkState(AbstractRightClick trigger) {
        currentInput = trigger;
        walkedMillis = 0;
    }

    private List<I2ClientMessage> takeInput(InputMessage input) {
        return Collections.emptyList();
    }

    @Override
    public List<I2ClientMessage> onRightMouseClicked(PlayerImpl player, RightMouseClick click) {
        return takeInput(click);
    }

    @Override
    public List<I2ClientMessage> onRightMouseReleased(PlayerImpl player, RightMouseRelease release) {
        return takeInput(release);
    }

    @Override
    public List<I2ClientMessage> handleMovementEvent(PlayerImpl player, CharacterMovementEvent event) {
        lastReceived = event;
        return Collections.emptyList();
    }

    @Override
    public State getState() {
        return State.WALK;
    }


    @Override
    public List<I2ClientMessage> OnRightMousePressedMotion(PlayerImpl player,
                                                           RightMousePressedMotion motion) {
        return takeInput(motion);
    }


    @Override
    public List<I2ClientMessage> update(PlayerImpl player, long deltaMillis) {
        if (walkedMillis == 0) {
            player.changeDirection(currentInput.direction());
        }
        walkedMillis += deltaMillis;
        if (walkedMillis < MILLIS_TO_WALK_ONE_UNIT) {
            return Collections.emptyList();
        }
        Coordinate newCoordinate = player.coordinate().moveBy(player.direction());
        if (!player.getRealm().canMoveTo(newCoordinate)) {
            player.changeState(new PlayerIdleState());
            return Collections.singletonList(SetPositionMessage.fromPlayer(player));
        }
        player.changeCoordinate(newCoordinate);
        log.debug("Moved to coordinate {}", newCoordinate);
        if (lastReceived == null) {
            log.debug("No more input, back to idle.");
            player.changeState(new PlayerIdleState());
            return Collections.emptyList();
        } else if (lastReceived.inputMessage() instanceof AbstractRightClick click) {
            log.debug("Continue moving due to event: {}.", lastReceived);
            return Collections.singletonList(Mover.onRightClick(player, click));
        } else if (lastReceived.inputMessage() instanceof RightMouseRelease) {
            log.debug("Go back to idle, event: {}.", lastReceived);
            player.changeState(new PlayerIdleState());
            return Collections.singletonList(new InputResponseMessage(lastReceived.inputMessage().sequence(), SetPositionMessage.fromPlayer(player)));
        }
        return Collections.emptyList();
    }

    @Override
    public Interpolation captureInterpolation(PlayerImpl player, long stateStartedAtMillis) {
        return null;
    }
}
