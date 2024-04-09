package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.message.*;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.input.*;
import org.y1000.util.Coordinate;

import java.util.*;

@Slf4j
final class PlayerWalkState implements PlayerState {

    private static final long MILLIS_TO_WALK_ONE_UNIT = 600;

    private final AbstractRightClick currentInput;

    private long walkedMillis;

    private CharacterMovementEvent lastReceived;

    public PlayerWalkState(AbstractRightClick trigger) {
        currentInput = trigger;
        walkedMillis = 0;
    }

    @Override
    public List<ServerEvent> handleMovementEvent(PlayerImpl player, CharacterMovementEvent event) {
        lastReceived = event;
        return Collections.emptyList();
    }

    @Override
    public State getState() {
        return State.WALK;
    }

    @Override
    public long elapsedMillis() {
        return walkedMillis;
    }

    @Override
    public List<ServerEvent> update(PlayerImpl player, long deltaMillis) {
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
}
