package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.AbstractCreatureIdleState;
import org.y1000.message.*;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.clientevent.ClientEventVisitor;
import org.y1000.message.input.AbstractRightClick;
import org.y1000.message.input.RightMouseRelease;

@Slf4j
final class PlayerIdleState extends AbstractCreatureIdleState<PlayerImpl>
        implements PlayerState, ClientEventVisitor {
    private static final int STATE_MILLIS = 3000;

    public PlayerIdleState() {
        super(STATE_MILLIS);
    }

    @Override
    public void update(PlayerImpl player, int deltaMillis) {
        resetIfElapsedLength(deltaMillis);
        player.takeClientEvent().ifPresent(e -> e.accept(player, this));
    }


    private void doAttack(PlayerImpl player, ClientAttackEvent event, Entity target) {
        player.attackKungFu().ifPresent(attackKungFu -> attackKungFu.attack(player, event, target));
    }

    @Override
    public void visit(PlayerImpl player,
                      ClientAttackEvent event) {
        player.realm().findInsight(player, event.entityId())
                .ifPresent(target -> doAttack(player, event, target));
    }

    @Override
    public void visit(PlayerImpl player, CharacterMovementEvent movementEvent) {
        log.debug("Handling event {} at {}.", movementEvent, player.coordinate());
        if (!movementEvent.happenedAt().equals(player.coordinate())) {
            player.reset(movementEvent.inputMessage().sequence());
            return;
        }
        if (movementEvent.inputMessage() instanceof AbstractRightClick rightClick) {
            player.emitEvent(Mover.onRightClick(player, rightClick));
        } else if (movementEvent.inputMessage() instanceof RightMouseRelease rightMouseRelease) {
            player.emitEvent(new InputResponseMessage(rightMouseRelease.sequence(), SetPositionEvent.fromPlayer(player)));
        }
        log.debug("Handled event {} at {}.", movementEvent, player.coordinate());
    }
}
