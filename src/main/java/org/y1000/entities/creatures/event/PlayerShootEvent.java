package org.y1000.entities.creatures.event;

import org.y1000.entities.projectile.PlayerProjectile;
import org.y1000.event.EntityEventVisitor;

public class PlayerShootEvent extends AbstractShootEvent {

    public PlayerShootEvent(PlayerProjectile projectile) {
        super(projectile.getShooter(), projectile);
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }

}
