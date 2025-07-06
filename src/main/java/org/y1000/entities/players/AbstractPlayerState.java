package org.y1000.entities.players;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.AbstractCreatureState;

abstract class AbstractPlayerState extends AbstractCreatureState implements PlayerState {

    private final PlayerInternal player;

    private final PlayerStateEnum stateEnum;

    public AbstractPlayerState(PlayerInternal player,
                               PlayerStateEnum stateEnum,
                               int stateMillis) {
        super(stateMillis);
        Validate.notNull(player);
        Validate.notNull(stateEnum);
        this.player = player;
        this.stateEnum = stateEnum;
    }

    protected PlayerInternal player() {
        return player;
    }

    @Override
    public PlayerStateEnum playerStateEnum() {
        return stateEnum;
    }
}
