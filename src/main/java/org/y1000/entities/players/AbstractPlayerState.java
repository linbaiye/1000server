package org.y1000.entities.players;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.creatures.AbstractCreatureState;
import org.y1000.entities.creatures.OldPlayerStateEnum;

public abstract class AbstractPlayerState extends AbstractCreatureState implements PlayerState {

    private final Player player;

    private final OldPlayerStateEnum stateEnum;

    public AbstractPlayerState(Player player,
                               OldPlayerStateEnum stateEnum,
                               int stateMillis) {
        super(stateMillis);
        Validate.notNull(player);
        Validate.notNull(stateEnum);
        this.player = player;
        this.stateEnum = stateEnum;
    }

    protected Player player() {
        return player;
    }

    public OldPlayerStateEnum stateEnum() {
        return stateEnum;
    }
}
