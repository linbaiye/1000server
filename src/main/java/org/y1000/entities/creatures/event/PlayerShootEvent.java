package org.y1000.entities.creatures.event;

import org.y1000.entities.projectile.PlayerProjectile;
import org.y1000.event.EntityEventVisitor;

public final class PlayerShootEvent extends AbstractShootEvent {

    public PlayerShootEvent(PlayerProjectile projectile) {
        super(projectile.getShooter(), projectile);
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
    }
}
