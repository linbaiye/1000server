package org.y1000.entities.creatures.npc.event;

import lombok.Getter;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.creatures.npc.Npc;
import org.y1000.entities.players.Damage;
import org.y1000.entities.projectile.NpcProjectile;
import org.y1000.message.I2ClientMessage;
import org.y1000.network.gen.Packet;
import org.y1000.network.gen.ProjectilePacket;
import org.y1000.realm.NpcEventHandler;

public class NpcShootEvent extends AbstractNpcEvent implements I2ClientMessage  {

    private final Packet packet;

    @Getter
    private final NpcProjectile npcProjectile;

    public NpcShootEvent(Npc npc, Packet packet, NpcProjectile npcProjectile) {
        super(npc);
        this.packet = packet;
        this.npcProjectile = npcProjectile;
    }

    @Override
    public void accept(NpcEventHandler handler) {
        handler.shoot(this);
    }

    public static NpcShootEvent of(Npc npc, NpcProjectile projectile) {
        ProjectilePacket packet1 = ProjectilePacket.newBuilder()
                .setId(npc.id())
                .setSprite(projectile.sprite())
                .setTargetId(projectile.target().id())
                .setFlyingTimeMillis(projectile.flyingMillis())
                .build();
        Packet p = Packet.newBuilder().setProjectile(packet1).build();
        return new NpcShootEvent(npc, p, projectile);
    }

    @Override
    public Packet toPacket() {
        return packet;
    }
}
