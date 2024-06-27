package org.y1000.entities.players;

import org.y1000.entities.creatures.AbstractCreatureState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.event.PlayerReviveEvent;

public final class PlayerDeadState extends AbstractCreatureState<PlayerImpl> implements PlayerState {

    public PlayerDeadState(int totalMillis) {
        super(totalMillis);
    }

    @Override
    public State stateEnum() {
        return State.DIE;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapse(delta)) {
            player.changeState(PlayerStillState.idle(player));
            player.emitEvent(new PlayerReviveEvent(player));
        }
    }

    @Override
    public boolean attackable() {
        return false;
    }

    public static PlayerDeadState die(PlayerImpl player) {
        return new PlayerDeadState(player.getStateMillis(State.DIE) + 30000);
    }

}
