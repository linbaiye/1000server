package org.y1000.entities.players;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AbstractCreatureMoveState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.event.RewindEvent;
import org.y1000.message.clientevent.ClientMovementEvent;
import org.y1000.util.Coordinate;

public abstract class AbstractPlayerMoveState extends AbstractCreatureMoveState<PlayerImpl> implements PlayerState {

    private ClientMovementEvent event;

    protected AbstractPlayerMoveState(State state, Coordinate start,
                                   Direction towards, int millisPerUnit) {
        super(state, start, towards, millisPerUnit);
    }

    protected abstract PlayerState rewindState(PlayerImpl player);

    protected abstract void onMoved(PlayerImpl player);

    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapsedMillis() == 0) {
            player.realmMap().free(player);
        }
        if (!walkMillis(player, delta)) {
            return;
        }
        if (tryChangeCoordinate(player, player.realmMap())) {
            onMoved(player);
            if (event != null && player.state() instanceof MovableState movableState) {
                movableState.move(player, event);
            }
        } else {
            player.changeCoordinate(getStart());
            player.changeState(rewindState(player));
            player.emitEvent(RewindEvent.of(player));
        }
    }

    @Override
    public void moveToHurtCoordinate(PlayerImpl creature) {
        tryChangeCoordinate(creature, creature.realmMap());
    }

    public void onMoveEvent(ClientMovementEvent event) {
        this.event = event;
    }

    @Override
    public void afterHurt(PlayerImpl player) {
        player.changeState(rewindState(player));
    }

    @Override
    public String toString() {
        return "Move[" + stateEnum() + "]";
    }
}
