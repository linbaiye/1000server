package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.y1000.message.*;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.input.AbstractRightClick;

import java.util.Collections;
import java.util.List;

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
            return Collections.singletonList(Mover.onRightClick(player, rightClick));
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

    @Override
    public List<ServerEvent> update(PlayerImpl player, long deltaMillis) {
        elapsedMillis += deltaMillis;
        if (elapsedMillis >= STATE_MILLIS)
            elapsedMillis = 0;
        return Collections.emptyList();
    }

}
