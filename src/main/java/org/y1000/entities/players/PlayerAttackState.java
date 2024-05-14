package org.y1000.entities.players;

import lombok.Getter;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;

public final class PlayerAttackState extends AbstractCreateState<PlayerImpl> implements PlayerState {

    private final Entity target;

    @Getter
    private final State state;

    private final int cooldownLength;

    public PlayerAttackState(int length,
                             Entity target,
                             State state,
                             int cooldownLength) {
        super(length);
        this.target = target;
        this.state = state;
        this.cooldownLength = cooldownLength;
    }

    @Override
    public State stateEnum() {
        return state;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (!elapse(delta)) {
            return;
        }
        if (cooldownLength > 0) {
            player.changeState(new PlayerCooldownState(cooldownLength, target));
        } else {
            player.attackKungFu().ifPresent(attackKungFu -> attackKungFu.attack(player, target));
        }
    }

    public static PlayerAttackState attack(Entity target, State state, int length, int cooldown) {
        if (cooldown < 0) {
            cooldown = 0;
        }
        return new PlayerAttackState(length, target, state, cooldown);
    }

}
