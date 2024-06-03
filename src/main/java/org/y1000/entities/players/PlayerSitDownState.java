package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.fight.AttackableState;

@Slf4j
public class PlayerSitDownState extends AbstractCreateState<PlayerImpl> implements PlayerState, AttackableState {

    public PlayerSitDownState(int totalMillis) {
        super(totalMillis);
    }

    @Override
    public State stateEnum() {
        return State.SIT;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapsedMillis() >= getTotalMillis()) {
            return;
        }
        elapse(delta);
    }


    @Override
    public boolean canStandUp() {
        return elapsedMillis() >= getTotalMillis();
    }

    @Override
    public boolean canUseFootKungFu() {
        return elapsedMillis() >= getTotalMillis();
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
    public State decideAfterHurtState(PlayerImpl player) {
        return State.SIT;
    }

    @Override
    public Logger logger() {
        return log;
    }
}
