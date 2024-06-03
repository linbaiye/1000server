package org.y1000.entities.players;

import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.fight.AttackableState;
;

/**
 * State that does not move.
 */
public abstract class AbstractPlayerStillState extends AbstractCreateState<PlayerImpl> implements
        AttackableState, MovableState, PlayerState {
    private final State state;

    public AbstractPlayerStillState(int totalMillis, State state) {
        super(totalMillis);
        this.state = state;
    }

    @Override
    public State stateEnum() {
        return state;
    }

    protected void elapseAndHandleInput(PlayerImpl player, int delta) {
        if (elapse(delta)) {
            reset();
        }
    }

    @Override
    public boolean canSitDown() {
        return true;
    }

    @Override
    public PlayerState stateForStuckMoving(PlayerImpl player) {
        reset();
        return this;
    }


    @Override
    public void afterHurt(PlayerImpl player) {
        player.changeState(this);
    }
}
