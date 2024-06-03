package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.MovableState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;
import org.y1000.message.PlayerTextEvent;

@Slf4j
public class PlayerWaitResourceState extends AbstractCreateState<PlayerImpl> implements PlayerState, MovableState {

    public PlayerWaitResourceState(int totalMillis) {
        super(totalMillis);
    }

    @Override
    public State stateEnum() {
        return State.COOLDOWN;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (!elapse(delta)) {
            return;
        }
        if (player.attackKungFu().hasEnoughResources(player)) {
            player.changeState(PlayerAttackState.of(player));
        } else {
            player.emitEvent(PlayerTextEvent.unableToAttack(player));
            player.changeState(new PlayerWaitResourceState(player.getStateMillis(State.COOLDOWN)));
        }
    }

    @Override
    public Logger logger() {
        return log;
    }

    @Override
    public PlayerState stateForStuckMoving(PlayerImpl player) {
        return null;
    }

    @Override
    public PlayerState stateForMove(PlayerImpl player, Direction direction) {
        return null;
    }

    public static PlayerWaitResourceState of(PlayerImpl player) {
        return new PlayerWaitResourceState(player.getStateMillis(State.COOLDOWN));
    }
}
