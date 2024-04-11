package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.y1000.message.*;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.message.input.AbstractRightClick;
import org.y1000.message.input.RightMouseRelease;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
final class PlayerIdleState implements PlayerState {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerState.class);

    private long elapsedMillis = 0;

    private static final long STATE_MILLIS = 3000;

    public PlayerIdleState() {
    }

    @Override
    public State getState() {
        return State.IDLE;
    }

    @Override
    public long elapsedMillis() {
        return elapsedMillis;
    }

    private boolean handleMovementEvent(PlayerImpl player, ClientEvent clientEvent) {
        if (clientEvent instanceof CharacterMovementEvent movementEvent) {
            if (movementEvent.inputMessage() instanceof AbstractRightClick rightClick) {
                player.emitEvent(Mover.onRightClick(player, rightClick));
            } else if (movementEvent.inputMessage() instanceof RightMouseRelease rightMouseRelease) {
                player.emitEvent(new InputResponseMessage(rightMouseRelease.sequence(), SetPositionEvent.fromPlayer(player)));
            }
            log.debug("Handling event {} at {}.", clientEvent, player.coordinate());
            return true;
        }
        return false;
    }

    @Override
    public void update(PlayerImpl player, long deltaMillis) {
        if (elapsedMillis >= STATE_MILLIS)
            elapsedMillis = 0;
        elapsedMillis += deltaMillis;
        while (player.hasClientEvent()) {
            ClientEvent clientEvent = player.takeClientEvent();
            if (handleMovementEvent(player, clientEvent))
                break;
        }
    }

}
