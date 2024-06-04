package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;

@Slf4j
public final class PlayerSitDownState extends AbstractCreateState<PlayerImpl> implements PlayerState {

    public PlayerSitDownState(int totalMillis) {
        super(totalMillis);
    }

    @Override
    public State stateEnum() {
        return State.SIT;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapsedMillis() >= totalMillis()) {
            return;
        }
        elapse(delta);
    }


    @Override
    public boolean canStandUp() {
        return elapsedMillis() >= totalMillis();
    }

    @Override
    public boolean canUseFootKungFu() {
        return elapsedMillis() >= totalMillis();
    }

    @Override
    public void afterHurt(PlayerImpl player) {
        reset();
        player.changeState(this);
    }

    public static PlayerSitDownState sit(PlayerImpl player) {
        return new PlayerSitDownState(player.getStateMillis(State.SIT));
    }

    @Override
    public State decideAfterHurtState() {
        return State.SIT;
    }
}
