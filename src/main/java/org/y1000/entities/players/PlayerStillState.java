package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.State;
import org.y1000.kungfu.FootKungFu;

import java.util.Optional;

@Slf4j
public final class PlayerStillState extends AbstractPlayerStillState {

    public PlayerStillState(int millis) {
        this(millis, State.IDLE);
    }

    public PlayerStillState(int millis, State state) {
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
    public PlayerState moveState(PlayerImpl player, Direction direction) {
        if (stateEnum() == State.COOLDOWN) {
            return PlayerMoveState.moveBy(player, State.ENFIGHT_WALK, direction);
        }
        Optional<FootKungFu> footMagic = player.footKungFu();
        State state = footMagic.map(magic -> magic.canFly() ? State.FLY : State.RUN)
                .orElse(State.WALK);
        return PlayerMoveState.moveBy(player, state, direction);
    }


    public static PlayerStillState idle(PlayerImpl player) {
        return new PlayerStillState(player.getStateMillis(State.IDLE));
    }

    public static PlayerStillState chillOut(PlayerImpl player) {
        return new PlayerStillState(player.getStateMillis(State.COOLDOWN), State.COOLDOWN);
    }


    @Override
    public String toString() {
        return stateEnum().name();
    }

    @Override
    public State decideAfterHurtState() {
        return stateEnum();
    }
}
