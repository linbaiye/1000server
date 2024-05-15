package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AbstractCreatureMoveState;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.creatures.CreatureState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.event.RewindEvent;
import org.y1000.message.clientevent.ClientMovementEvent;
import org.y1000.message.clientevent.ClientEventVisitor;
import org.y1000.util.Coordinate;

import java.util.Set;


@Slf4j
public final class PlayerMoveState extends AbstractCreatureMoveState<PlayerImpl>
        implements ClientEventVisitor, MovableState {

    private static final Set<State> MOVE_STATES = Set.of(
            State.WALK, State.RUN, State.FLY, State.ENFIGHT_WALK
    );

    private PlayerMoveState(State state, Coordinate start, Direction towards,  int millisPerUnit) {
        super(state, start, towards, millisPerUnit);
    }

    private void handleInput(PlayerImpl player) {
        player.takeClientEvent().ifPresent(e -> e.accept(player, this));
    }

    @Override
    public void update(PlayerImpl player, int deltaMillis) {
        if (elapsedMillis() >= getTotalMillis()) {
            handleInput(player);
            return;
        }
        walkMillis(player, deltaMillis);
        if (elapsedMillis() < getTotalMillis()) {
            return;
        }
        if (tryChangeCoordinate(player, player.realmMap())) {
            handleInput(player);
            return;
        }
        player.changeCoordinate(getStart());
        player.changeState(stateForRewind(player));
        player.clearEventQueue();
        player.emitEvent(RewindEvent.of(player));
    }

    @Override
    public void afterAttacked(PlayerImpl player, Creature attacker) {
        player.changeState(this);
    }

    @Override
    public void visit(PlayerImpl player, ClientMovementEvent event) {
        move(player, event);
    }


    @Override
    public CreatureState<PlayerImpl> stateForStopMoving(PlayerImpl player) {
        if (stateEnum() == State.ENFIGHT_WALK) {
            return new PlayerCooldownState(player.getStateMillis(State.COOLDOWN), null);
        } else {
            return PlayerIdleState.of(player);
        }
    }

    @Override
    public CreatureState<PlayerImpl> stateForMove(PlayerImpl player, Direction direction) {
        return moveBy(player, stateEnum(), direction);
    }

    public static PlayerMoveState moveBy(PlayerImpl player, State state, Direction direction) {
        if (!MOVE_STATES.contains(state)) {
            throw new IllegalArgumentException("Not a move state: " + state);
        }
        return new PlayerMoveState(state, player.coordinate(), direction, player.getStateMillis(state));
    }
}
