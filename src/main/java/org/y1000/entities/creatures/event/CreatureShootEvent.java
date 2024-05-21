package org.y1000.entities.creatures.event;

import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.Projectile;
import org.y1000.message.serverevent.EntityEvent;
import org.y1000.message.serverevent.EntityEventVisitor;

public record CreatureShootEvent(Projectile projectile) implements EntityEvent {

    @Override
    public PhysicalEntity source() {
        return projectile.getShooter();
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
