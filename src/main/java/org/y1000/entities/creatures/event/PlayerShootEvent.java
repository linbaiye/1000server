package org.y1000.entities.creatures.event;

import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.PlayerProjectile;
import org.y1000.message.serverevent.EntityEventVisitor;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.PlayerProjectilePacket;

public class PlayerShootEvent extends AbstractCreatureEvent {
    private final PlayerProjectile projectile;

    public PlayerShootEvent(PlayerProjectile projectile) {
        super(projectile.getShooter());
        this.projectile = projectile;
    }

    @Override
    public PhysicalEntity source() {
        return projectile.getShooter();
    }

    @Override
    public void accept(EntityEventVisitor visitor) {
        visitor.visit(this);
    }

    public PlayerProjectile projectile() {
        return projectile;
    }

    @Override
    protected Packet buildPacket() {
        return Packet.newBuilder().setPlayerProjectile(
                PlayerProjectilePacket.newBuilder()
                        .setId(source().id())
                        .setTargetId(projectile.target().id())
                        .setFlyingTimeMillis(projectile.flyingMillis())
                        .build()
                ).build();
    }
}
