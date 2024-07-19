package org.y1000.entities.creatures.event;

import org.y1000.entities.projectile.NpcProjectile;
import org.y1000.event.EntityEventVisitor;


public class MonsterShootEvent extends AbstractShootEvent {
    public MonsterShootEvent(NpcProjectile projectile) {
        super(projectile.getShooter(), projectile);
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }
}
