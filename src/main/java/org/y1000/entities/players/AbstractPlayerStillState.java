package org.y1000.entities.players;

import org.y1000.entities.creatures.IAbstractCreatureState;
import org.y1000.entities.creatures.PlayerStateEnum;
;

/**
 * State that does not move.
 */
public abstract class AbstractPlayerStillState extends IAbstractCreatureState<PlayerImpl> implements
        MovableState, IPlayerState {
    private final PlayerStateEnum playerStateEnum;

    public AbstractPlayerStillState(int totalMillis, PlayerStateEnum playerStateEnum) {
        super(totalMillis);
        this.playerStateEnum = playerStateEnum;
    }

    @Override
    public PlayerStateEnum stateEnum() {
        return playerStateEnum;
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
    public IPlayerState rewindState(PlayerImpl player) {
        reset();
        return this;
    }



    @Override
    public void afterHurt(PlayerImpl player) {
        player.changeState(this);
    }
}
