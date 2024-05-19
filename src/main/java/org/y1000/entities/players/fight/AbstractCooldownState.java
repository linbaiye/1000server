package org.y1000.entities.players.fight;

import org.y1000.entities.Entity;
import org.y1000.entities.creatures.State;
import org.y1000.entities.players.AbstractPlayerStillState;
import org.y1000.entities.players.PlayerImpl;

public abstract class AbstractCooldownState extends AbstractPlayerStillState {

    private final Entity target;


    public AbstractCooldownState(int length, Entity target) {
        super(length, State.COOLDOWN);
        this.target = target;
    }

    protected Entity getTarget() {
        return target;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapse(delta)) {
            attack(player, target);
        } else {
            player.takeClientEvent().ifPresent(e -> e.accept(player, this));
        }
    }
}
