package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.MovableState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;
import org.y1000.entities.players.PlayerStillState;

@Slf4j
public class PlayerWaitDistanceState extends AbstractCreateState<PlayerImpl> implements PlayerState, MovableState  {

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

    public void onFightingEntityEvent(PlayerImpl player) {
        if (!player.isFighting() || !player.getFightingEntity().attackable()) {
            player.changeState(PlayerStillState.chillOut(player));
            return;
        }
        if (player.coordinate().directDistance(player.getFightingEntity().coordinate()) <= 1) {
            player.attackKungFu().attackAgain(player);
        }
    }

    @Override
    public Logger logger() {
        return null;
    }

    @Override
    public PlayerState stateForMove(PlayerImpl player, Direction direction) {
        return null;
    }
}
