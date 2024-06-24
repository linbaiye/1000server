package org.y1000.entities.creatures.event;

import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.Creature;
import org.y1000.entities.projectile.AbstractProjectile;
import org.y1000.entities.projectile.Projectile;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ProjectilePacket;

public abstract class AbstractShootEvent extends AbstractCreatureEvent {

    private final Projectile projectile;

    protected AbstractShootEvent(Creature source,
                                 AbstractProjectile projectile) {
        super(source);
        this.projectile = projectile;
    }

    public Projectile projectile() {
        return projectile;
    }

    @Override
    public PhysicalEntity source() {
        return projectile.shooter();
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder().setProjectile(
                ProjectilePacket.newBuilder()
                        .setId(source().id())
                        .setTargetId(projectile.target().id())
                        .setSprite(projectile.projectileSpriteId())
                        .setFlyingTimeMillis(projectile.flyingMillis())
                        .build()
        ).build();
    }
}
