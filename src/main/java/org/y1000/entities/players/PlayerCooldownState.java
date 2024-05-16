package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;

@Slf4j
final class PlayerCooldownState extends AbstractPlayerIdleState {

    private final Entity target;

    public PlayerCooldownState(int length, Entity target) {
        super(length, State.COOLDOWN);
        this.target = target;
    }


    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapse(delta)) {
            player.attack(target);
        } else {
            player.takeClientEvent().ifPresent(e -> e.accept(player, this));
        }
    }

    @Override
    public Logger logger() {
        return log;
    }

    @Override
    public PlayerState stateForStopMoving(PlayerImpl player) {
        return new PlayerCooldownState(player.getStateMillis(stateEnum()), target);
    }

    @Override
    public PlayerState stateForMove(PlayerImpl player, Direction direction) {
        return PlayerEnfightWalkState.move(player, direction, target);
    }
}
