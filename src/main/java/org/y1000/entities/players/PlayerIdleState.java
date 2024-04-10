package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.y1000.message.*;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.message.input.AbstractRightClick;

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
    public List<ServerEvent> handleMovementEvent(PlayerImpl player, CharacterMovementEvent event) {
        if (event.inputMessage() instanceof AbstractRightClick rightClick) {
            InputResponseMessage message = Mover.onRightClick(player, rightClick);
            player.emitEvent(message.positionMessage());
        }
        return Collections.emptyList();
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
                InputResponseMessage message = Mover.onRightClick(player, rightClick);
                player.emitEvent(message.positionMessage());
            }
            return true;
        }
        return false;
    }

    @Override
    public List<ServerEvent> update(PlayerImpl player, long deltaMillis) {
        elapsedMillis += deltaMillis;
        if (elapsedMillis >= STATE_MILLIS)
            elapsedMillis = 0;
        while (player.hasClientEvent()) {
            ClientEvent clientEvent = player.takeClientEvent();
            if (handleMovementEvent(player, clientEvent))
                break;
        }
        return Collections.emptyList();
    }

}
