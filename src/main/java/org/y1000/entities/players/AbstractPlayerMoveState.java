package org.y1000.entities.players;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AbstractCreatureMoveState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.event.RewindEvent;
import org.y1000.util.Coordinate;

public abstract class AbstractPlayerMoveState extends AbstractCreatureMoveState<PlayerImpl> implements PlayerState {
    protected AbstractPlayerMoveState(State state, Coordinate start,
                                   Direction towards, int millisPerUnit) {
        super(state, start, towards, millisPerUnit);
    }

    protected abstract PlayerState stopMovingState(PlayerImpl player);

    @Override
    public void update(PlayerImpl player, int delta) {
        if (!walkMillis(player, delta)) {
            return;
        }
        if (tryChangeCoordinate(player, player.realmMap())) {
            player.changeState(stopMovingState(player));
            // no event here since we should receive more following inputs immediately.
        } else {
            player.changeCoordinate(getStart());
            player.changeState(stopMovingState(player));
            player.clearEventQueue();
            player.emitEvent(RewindEvent.of(player));
        }
    }

    @Override
    public void afterHurt(PlayerImpl player) {
        player.changeState(this);
    }
}
