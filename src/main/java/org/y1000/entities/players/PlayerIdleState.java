package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.kungfu.FootKungFu;
import java.util.Optional;

@Slf4j
final class PlayerIdleState extends AbstractPlayerIdleState {

    public PlayerIdleState(int millis) {
        this(millis, State.IDLE);
    }

    public PlayerIdleState(int millis, State state) {
        super(millis, state);
    }

    @Override
    public void update(PlayerImpl player, int deltaMillis) {
        elapseAndHandleInput(player, deltaMillis);
    }

    @Override
    public Logger logger() {
        return log;
    }

    @Override
    public PlayerState stateForMove(PlayerImpl player, Direction direction) {
        if (stateEnum() == State.COOLDOWN) {
            return PlayerMoveState.moveBy(player, State.ENFIGHT_WALK, direction);
        }
        Optional<FootKungFu> footMagic = player.footKungFu();
        State state = footMagic.map(magic -> magic.canFly() ? State.FLY : State.RUN)
                .orElse(State.WALK);
        return PlayerMoveState.moveBy(player, state, direction);
    }

    public static PlayerIdleState of(PlayerImpl player) {
        return new PlayerIdleState(player.getStateMillis(State.IDLE));
    }

    public static PlayerIdleState chillOut(PlayerImpl player) {
        return new PlayerIdleState(player.getStateMillis(State.COOLDOWN));
    }
}
