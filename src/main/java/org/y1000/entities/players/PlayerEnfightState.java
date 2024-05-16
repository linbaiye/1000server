package org.y1000.entities.players;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;

@Slf4j
final class PlayerEnfightState extends AbstractPlayerIdleState {
    private final Entity target;
    public PlayerEnfightState(int totalMillis, Entity target) {
        super(totalMillis, State.COOLDOWN);
        this.target = target;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (target.coordinate().distance(player.coordinate()) <= 1) {
            player.attack(target);
        }
        elapseAndHandleInput(player, delta);
    }

    @Override
    public Logger logger() {
        return log;
    }

    @Override
    public PlayerState stateForMove(PlayerImpl player, Direction direction) {
        return PlayerEnfightWalkState.move(player, direction, target);
    }
}
