package org.y1000.entities.players;

import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.ClientEventVisitor;
import org.y1000.message.clientevent.ClientMovementEvent;

public abstract class AbstractPlayerIdleState extends AbstractCreateState<PlayerImpl> implements
        ClientEventVisitor, AttackableState, MovableState, PlayerState {
    private final State state;

    public AbstractPlayerIdleState(int totalMillis, State state) {
        super(totalMillis);
        this.state = state;
    }

    @Override
    public State stateEnum() {
        return state;
    }

    @Override
    public void visit(PlayerImpl player, ClientAttackEvent event) {
        attackIfInsight(player, event);
    }

    @Override
    public void visit(PlayerImpl player, ClientMovementEvent event) {
        move(player, event);
    }

    protected void elapseAndHandleInput(PlayerImpl player, int delta) {
        if (elapse(delta)) {
            reset();
        }
        player.takeClientEvent().ifPresent(e -> e.accept(player, this));
    }


    @Override
    public PlayerState stateForStopMoving(PlayerImpl player) {
        reset();
        return this;
    }

    @Override
    public void afterHurt(PlayerImpl player) {
        player.changeState(this);
    }
}
