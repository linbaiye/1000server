package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.AbstractCreatureIdleState;
import org.y1000.message.*;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.message.input.AbstractRightClick;
import org.y1000.message.input.RightMouseRelease;

@Slf4j
final class PlayerIdleState extends AbstractCreatureIdleState<PlayerImpl>
        implements PlayerState {
    private static final long STATE_MILLIS = 3000;

    public PlayerIdleState() {
        super(STATE_MILLIS);
    }

    private boolean handleMovementEvent(PlayerImpl player, ClientEvent clientEvent) {
        if (clientEvent instanceof CharacterMovementEvent movementEvent) {
            if (!movementEvent.happenedAt().equals(player.coordinate())) {
                player.reset(movementEvent.inputMessage().sequence());
                return true;
            }
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
        resetIfElapsedLength(deltaMillis);
        while (player.hasClientEvent()) {
            ClientEvent clientEvent = player.takeClientEvent();
            if (handleMovementEvent(player, clientEvent))
                break;
        }
    }
}
