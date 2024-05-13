package org.y1000.entities.players;
import org.y1000.entities.creatures.*;

public final class PlayerHurtState extends AbstractCreatureHurtState<PlayerImpl> implements PlayerState {

    private final Creature attacker;

    public PlayerHurtState(int totalMillis, Creature attacker) {
        super(totalMillis);
        this.attacker = attacker;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (!elapse(delta)) {
            return;
        }
        if (player.getRecoveryCooldown() > 0 || player.getAttackCooldown() > 0) {
            player.changeState(PlayerCooldownState.cooldown(player, attacker));
        } else {
            player.attackKungFu().ifPresent(attackKungFu -> attackKungFu.attack(player, attacker));
        }
    }

    public static PlayerHurtState attackedBy(PlayerImpl player, Creature attacker) {
        int stateMillis = player.getStateMillis(State.HURT);
        return new PlayerHurtState(stateMillis, attacker);
    }
}
