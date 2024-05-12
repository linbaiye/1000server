package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.AbstractCreatureIdleState;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.clientevent.ClientEventVisitor;

@Slf4j
public final class PlayerIdleState extends AbstractCreatureIdleState<PlayerImpl>
        implements PlayerState, ClientEventVisitor {
    private static final int STATE_MILLIS = 3000;

    public PlayerIdleState() {
        super(STATE_MILLIS);
    }

    @Override
    public void update(PlayerImpl player, int deltaMillis) {
        if (elapse(deltaMillis)) {
            reset();
        }
        player.takeClientEvent().ifPresent(e -> e.accept(player, this));
    }


    @Override
    public void visit(PlayerImpl player,
                      ClientAttackEvent event) {
        attackIfHasKungfu(player, event);
    }

    @Override
    public void visit(PlayerImpl player, CharacterMovementEvent movementEvent) {
        movementEvent.resetOrMove(player);
    }
}
