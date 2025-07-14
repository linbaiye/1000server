package org.y1000.entities.players.event;

import lombok.Getter;
import org.y1000.entities.players.Player;
import org.y1000.entities.projectile.PlayerProjectile;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ProjectilePacket;
import org.y1000.realm.PlayerEventHandler;

public final class PlayerLetFlyProjectileEvent extends AbstractMessagePlayerEvent {

    @Getter
    private final PlayerProjectile projectile;

    public PlayerLetFlyProjectileEvent(Player player, Packet packet,
                                       PlayerProjectile projectile) {
        super(player, packet);
        this.projectile = projectile;
    }

    public static PlayerLetFlyProjectileEvent of(Player player,
                                                 PlayerProjectile projectile) {
        ProjectilePacket projectilePacket = ProjectilePacket.newBuilder()
                .setFlyingTimeMillis(projectile.flyingMillis())
                .setSprite(projectile.sprite())
                .setTargetId(projectile.target().id())
                .setId(player.id())
                .build();
        Packet packet = Packet.newBuilder().setProjectile(projectilePacket).build();
        return new PlayerLetFlyProjectileEvent(player, packet, projectile);
    }

    @Override
    public void accept(PlayerEventHandler handler) {
        handler.onPlayerFireProjectile(this);
    }
}
