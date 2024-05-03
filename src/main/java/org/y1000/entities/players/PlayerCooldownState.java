package org.y1000.entities.players;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.AbstractCreatureCooldownState;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.ClientEventVisitor;

public final class PlayerCooldownState extends AbstractCreatureCooldownState<PlayerImpl> implements PlayerState, ClientEventVisitor {

    private final int length;

    private final Entity target;

    public PlayerCooldownState(int length, Entity target) {
        this.length = length;
        this.target = target;
    }

    public void attackAgain(PlayerImpl player) {
        player.attackKungFu().ifPresent(attackKungFu -> attackKungFu.attack(player, target));
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        elapse(delta);
        if (elapsedMillis() >= length) {
            attackAgain(player);
        } else {
            player.takeClientEvent().ifPresent(e -> e.accept(player, this));
        }
    }

    @Override
    public void visit(PlayerImpl player, ClientAttackEvent event) {
        attackIfHasKungfu(player, event);
    }

    @Override
    public void visit(PlayerImpl player, CharacterMovementEvent event) {

    }
}
