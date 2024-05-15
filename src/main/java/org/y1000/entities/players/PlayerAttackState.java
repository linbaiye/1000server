package org.y1000.entities.players;

import lombok.Getter;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;

public final class PlayerAttackState extends AbstractCreateState<PlayerImpl> implements AttackableState, PlayerState {

    private final Entity target;

    @Getter
    private final State attackState;

    public PlayerAttackState(int length,
                             Entity target,
                             State state) {
        super(length);
        this.target = target;
        this.attackState = state;
    }

    @Override
    public State stateEnum() {
        return attackState;
    }

    @Override
    public void afterAttacked(PlayerImpl player) {
        player.attack(target);
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (!elapse(delta)) {
            return;
        }
        int cooldown = player.cooldown();
        if (cooldown > 0) {
            player.changeState(new PlayerCooldownState(cooldown, target));
        } else {
            player.attack(target);
        }
    }

    public static PlayerAttackState attack(Entity target, State state, int length) {
        return new PlayerAttackState(length, target, state );
    }

}
