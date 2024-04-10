package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.message.*;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.clientevent.ClientEvent;
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


    private void handleEvent(PlayerImpl player, ClientEvent clientEvent) {
        if (clientEvent instanceof CharacterMovementEvent movementEvent) {
            if (movementEvent.inputMessage() instanceof AbstractRightClick rightClick) {
                player.emitEvent(Mover.onRightClick(player, rightClick));
            } else if (lastReceived.inputMessage() instanceof RightMouseRelease rightMouseRelease) {
                player.emitEvent(new InputResponseMessage(rightMouseRelease.sequence(), SetPositionEvent.fromPlayer(player)));
            }
        } else {
            log.warn("Not a handleable event {}", clientEvent);
        }
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
            // May conflict with something.
            player.changeState(new PlayerIdleState());
            player.emitEvent(SetPositionEvent.fromPlayer(player));
            return Collections.emptyList();
        }
        player.changeCoordinate(newCoordinate);
        log.debug("Moved to coordinate {}", newCoordinate);
        var clientEvent = player.takeClientEvent();
        if (clientEvent == null) {
            log.debug("No more input, back to idle waiting for next input.");
            player.changeState(new PlayerIdleState());
            return Collections.emptyList();
        } else {
            handleEvent(player, clientEvent);
        }
        return Collections.emptyList();
    }
}
