package org.y1000.entities.players;

import org.y1000.entities.creatures.State;

final class PlayerFrozenState implements PlayerState {
    public static final PlayerFrozenState Instance = new PlayerFrozenState();

    private PlayerFrozenState() {}
    @Override
    public State stateEnum() {
        return State.IDLE;
    }

    @Override
    public int elapsedMillis() {
        return 0;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public boolean canAttack() {
        return false;
    }

    @Override
    public void update(PlayerImpl player, int delta) {

    }
}
