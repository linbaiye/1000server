package org.y1000.entities.creatures.event;

import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.projectile.AbstractProjectile;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ProjectilePacket;

public abstract class AbstractShootEvent extends AbstractCreatureEvent {

    private final AbstractProjectile<?> projectile;

    protected AbstractShootEvent(Creature source,
                                 AbstractProjectile<?> projectile) {
        super(source);
        this.projectile = projectile;
    }

    public AbstractProjectile<?> projectile() {
        return projectile;
    }


    @Override
    public PhysicalEntity source() {
        return projectile.getShooter();
    }


    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder().setProjectile(
                ProjectilePacket.newBuilder()
                        .setId(source().id())
                        .setTargetId(projectile.target().id())
                        .setSprite(projectile.spriteId())
                        .setFlyingTimeMillis(projectile.flyingMillis())
                        .build()
        ).build();
    }
}
