package org.y1000.entities.players;

import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;

public class PlayerStandUpState extends AbstractCreateState<PlayerImpl> implements PlayerState {

    public PlayerStandUpState(int totalMillis) {
        super(totalMillis);
    }

    public PlayerStandUpState(PlayerImpl player) {
        this(player.getStateMillis(State.STANDUP));
    }

    @Override
    public State stateEnum() {
        return State.STANDUP;
    }

    @Override
    public boolean canUseFootKungFu() {
        return elapsedMillis() >= getTotalMillis();
    }

    @Override
    public void afterHurt(PlayerImpl player) {
        player.changeState(PlayerStillState.idle(player));
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapse(delta)) {
            player.changeState(PlayerStillState.idle(player));
        }
    }
}
