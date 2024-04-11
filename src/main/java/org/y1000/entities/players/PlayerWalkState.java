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

    public PlayerWalkState(AbstractRightClick trigger) {
        currentInput = trigger;
        walkedMillis = 0;
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
            log.warn("Handling event {} at {}.", clientEvent, player.coordinate());
            if (movementEvent.inputMessage() instanceof AbstractRightClick rightClick) {
                player.emitEvent(Mover.onRightClick(player, rightClick));
            } else if (movementEvent.inputMessage() instanceof RightMouseRelease rightMouseRelease) {
                player.changeState(new PlayerIdleState());
                player.emitEvent(new InputResponseMessage(rightMouseRelease.sequence(), SetPositionEvent.fromPlayer(player)));
            }
        } else {
            log.warn("Not a handleable event {}", clientEvent);
        }
    }


    @Override
    public void update(PlayerImpl player, long deltaMillis) {
        if (walkedMillis == 0) {
            player.changeDirection(currentInput.direction());
        }
        walkedMillis += deltaMillis;
        if (walkedMillis < MILLIS_TO_WALK_ONE_UNIT) {
            return;
        }
        Coordinate newCoordinate = player.coordinate().moveBy(player.direction());
        if (!player.getRealm().canMoveTo(newCoordinate)) {
            // May conflict with something.
            player.changeState(new PlayerIdleState());
            player.emitEvent(new InputResponseMessage(currentInput.sequence(), SetPositionEvent.fromPlayer(player)));
            return;
        }
        player.changeCoordinate(newCoordinate);
        log.debug("Moved to coordinate {}", newCoordinate);
        var clientEvent = player.takeClientEvent();
        if (clientEvent == null) {
            log.debug("No more input, back to idle waiting for next input.");
            player.changeState(new PlayerIdleState());
        } else {
            handleEvent(player, clientEvent);
        }
    }
}
