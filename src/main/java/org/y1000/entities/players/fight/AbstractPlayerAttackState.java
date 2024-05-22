package org.y1000.entities.players.fight;

import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.MovableState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;
import org.y1000.message.clientevent.ClientAttackEvent;
import org.y1000.message.clientevent.BiClientEventVisitor;
import org.y1000.message.clientevent.ClientMovementEvent;

public abstract class AbstractPlayerAttackState extends AbstractCreateState<PlayerImpl> implements AttackableState,
        PlayerState, BiClientEventVisitor, MovableState {

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


    public abstract void weaponChanged(PlayerImpl player);


    @Override
    public State stateEnum() {
        return state;
    }

    @Override
    public State decideAfterHurtState() {
        return State.COOLDOWN;
    }

    @Override
    public void visit(PlayerImpl player, ClientAttackEvent event) {
        handleAttackEvent(player, event);
    }

    @Override
    public void visit(PlayerImpl player, ClientMovementEvent event) {
        move(player, event);
    }
}
