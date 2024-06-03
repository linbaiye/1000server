package org.y1000.entities.players.fight;

import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.MovableState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;

public abstract class AbstractPlayerAttackState extends AbstractCreateState<PlayerImpl> implements AttackableState,
        PlayerState, MovableState {

    private final State state;

    private final PhysicalEntity target;

    public AbstractPlayerAttackState(int totalMillis, PhysicalEntity target, State state) {
        super(totalMillis);
        this.state = state;
        this.target = target;
    }

    protected PhysicalEntity getTarget() {
        return target;
    }

    @Override
    public boolean canSitDown() {
        return true;
    }

    @Override
    public State stateEnum() {
        return state;
    }
}
