package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.PlayerImpl;

@Slf4j
public final class PlayerWaitDistanceState extends AbstractFightingState {

    public PlayerWaitDistanceState(int totalMillis) {
        super(totalMillis);
    }

    @Override
    public State stateEnum() {
        return State.COOLDOWN;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapse(delta)) {
            reset();
        }
    }

    public void onTargetEvent(PlayerImpl player) {
        player.attackKungFu().attackAgain(player);
    }

    @Override
    public Logger logger() {
        return log;
    }
}
