package org.y1000.entities.players;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.AbstractCreateState;
;

public class PlayerAttackState extends AbstractCreateState<PlayerImpl> implements PlayerState {
    private final int length;
    private final Entity target;
    private final int cooldownLength;

    public PlayerAttackState(int length,
                             Entity target,
                             int cooldownLength) {
        this.length = length;
        this.target = target;
        this.cooldownLength = cooldownLength;
    }


    @Override
    public State stateEnum() {
        return State.ATTACK;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        elapse(delta);
        if (elapsedMillis() < length) {
            return;
        }
        player.changeState(new PlayerCooldownState(cooldownLength, target));
    }
}
