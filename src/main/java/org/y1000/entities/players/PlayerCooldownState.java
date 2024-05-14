package org.y1000.entities.players;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.AbstractCreatureCooldownState;
import org.y1000.entities.creatures.Creature;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.clientevent.ClientEventVisitor;

public final class PlayerCooldownState extends AbstractCreatureCooldownState<PlayerImpl> implements PlayerState, ClientEventVisitor {

    private final Entity target;

    public PlayerCooldownState(int length, Entity target) {
        super(length);
        this.target = target;
    }


    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapse(delta)) {
            player.attack(target);
        } else {
            player.takeClientEvent().ifPresent(e -> e.accept(player, this));
        }
    }

    @Override
    public void afterAttacked(PlayerImpl player, Creature attacker) {
        if (player.getRecoveryCooldown() > 0 || player.getAttackCooldown() > 0) {
            player.changeState(cooldown(player, attacker));
        } else {

        }
    }

    @Override
    public void visit(PlayerImpl player, CharacterMovementEvent event) {

    }

    public static PlayerCooldownState cooldown(PlayerImpl player, Entity target) {
        int len = Math.max(player.getAttackCooldown(), player.getRecoveryCooldown());
        return new PlayerCooldownState(len, target);
    }
}
