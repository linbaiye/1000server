package org.y1000.entities.players;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.AbstractCreatureCooldownState;
import org.y1000.entities.creatures.Creature;
import org.y1000.message.clientevent.CharacterMovementEvent;
import org.y1000.message.clientevent.ClientAttackEvent;
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
            player.attackKungFu().ifPresent(attackKungFu -> attackKungFu.attack(player, target));
        } else {
            player.takeClientEvent().ifPresent(e -> e.accept(player, this));
        }
    }

    @Override
    public void visit(PlayerImpl player, CharacterMovementEvent event) {

    }

    public static PlayerCooldownState cooldown(PlayerImpl player, Creature attacker) {
        int len = Math.max(player.getAttackCooldown(), player.getRecoveryCooldown());
        return new PlayerCooldownState(len, attacker);
    }
}
