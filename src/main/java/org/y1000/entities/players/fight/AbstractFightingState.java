package org.y1000.entities.players.fight;

import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.players.MovableState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;

public abstract class AbstractFightingState extends AbstractCreateState<PlayerImpl> implements PlayerState, MovableState {


    public AbstractFightingState(int totalMillis) {
        super(totalMillis);
    }

    @Override
    public PlayerState stateForMove(PlayerImpl player, Direction direction) {
        return PlayerFightWalkState.walk(player, direction);
    }

    @Override
    public PlayerState stateForNotMovable(PlayerImpl player) {
        return this;
    }

    @Override
    public void afterHurt(PlayerImpl player) {
        player.changeState(new PlayerCooldownState(player.cooldown()));
    }
}
