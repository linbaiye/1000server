package org.y1000.entities.players;

import lombok.Getter;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.AbstractCreateState;

public final class PlayerAttackState extends AbstractCreateState<PlayerImpl> implements PlayerState {
    private final int length;
    private final Entity target;
    private final int cooldownLength;

    @Getter
    private final boolean below50;

    public PlayerAttackState(int length,
                             Entity target,
                             int cooldownMillisPerSprite,
                             boolean below50) {
        this.length = length;
        this.target = target;
        this.cooldownLength = cooldownMillisPerSprite * 3;
        this.below50 = below50;
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
