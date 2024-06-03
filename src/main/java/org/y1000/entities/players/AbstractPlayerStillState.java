package org.y1000.entities.players;

import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.ChangeStateEvent;
import org.y1000.entities.players.fight.AttackableState;
import org.y1000.kungfu.KungFu;
import org.y1000.kungfu.KungFuType;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.BiClientEventVisitor;
import org.y1000.message.clientevent.ClientEvent;
import org.y1000.message.clientevent.ClientMovementEvent;

/**
 * State that does not move.
 */
public abstract class AbstractPlayerStillState extends AbstractCreateState<PlayerImpl> implements
        BiClientEventVisitor, AttackableState, MovableState, PlayerState {
    private final State state;

    public AbstractPlayerStillState(int totalMillis, State state) {
        super(totalMillis);
        this.state = state;
    }

    @Override
    public State stateEnum() {
        return state;
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
    public boolean canSitDown() {
        return true;
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
