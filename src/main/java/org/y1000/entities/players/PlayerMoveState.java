package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.util.Coordinate;
import java.util.Set;

@Slf4j
final class PlayerMoveState extends AbstractPlayerMoveState {

    private static final Set<State> MOVE_STATES = Set.of(
            State.WALK, State.RUN, State.FLY, State.ENFIGHT_WALK
    );

    private PlayerMoveState(State state, Coordinate start, Direction towards,  int millisPerUnit) {
        super(state, start, towards, millisPerUnit);
    }

    @Override
    protected PlayerState rewindState(PlayerImpl player) {
        return stateEnum() == State.ENFIGHT_WALK ? PlayerStillState.chillOut(player) : PlayerStillState.idle(player);
    }

    @Override
    protected void onMoved(PlayerImpl player) {
        player.changeState(rewindState(player));
    }

    public static PlayerMoveState moveBy(PlayerImpl player, State state, Direction direction) {
        if (!MOVE_STATES.contains(state)) {
            throw new IllegalArgumentException("Not a move state: " + state);
        }
        return new PlayerMoveState(state, player.coordinate(), direction, player.getStateMillis(state));
    }

    public static PlayerMoveState moveBy(PlayerImpl player, Direction direction) {
        State state = player.footKungFu().map(kf -> kf.canFly() ? State.FLY : State.RUN).orElse(State.WALK);
        return moveBy(player, state, direction);
    }
}
