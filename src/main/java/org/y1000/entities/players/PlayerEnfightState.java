package org.y1000.entities.players;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;

public class PlayerEnfightState extends AbstractCreateState<PlayerImpl> {
    private final Entity target;
    public PlayerEnfightState(int totalMillis, Entity target) {
        super(totalMillis);
        this.target = target;
    }

    @Override
    public State stateEnum() {
        return State.COOLDOWN;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (target != null && target.coordinate().distance(player.coordinate()) <= 1) {
            player.attack(target);
        } else if (elapse(delta)) {
            reset();
        }
    }

}
