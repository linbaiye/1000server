package org.y1000.entities.creatures.event;

import org.y1000.entities.projectile.MonsterProjectile;
import org.y1000.event.EntityEventVisitor;


public class MonsterShootEvent extends AbstractShootEvent {
    public MonsterShootEvent(MonsterProjectile projectile) {
        super(projectile.getShooter(), projectile);
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
