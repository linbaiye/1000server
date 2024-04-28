package org.y1000.entities.players;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.AbstractCreateState;

public class PlayerAttackState extends AbstractCreateState<PlayerImpl> implements PlayerState {

    private final long length;

    private final Entity target;

    public PlayerAttackState(long length, Entity target) {
        this.length = length;
        this.target = target;
    }

    @Override
    public State stateEnum() {
        return State.ATTACK;
    }
    @Override
    public void update(PlayerImpl player, long delta) {

    }
}
