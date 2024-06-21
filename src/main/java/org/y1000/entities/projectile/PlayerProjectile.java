package org.y1000.entities.projectile;


import lombok.Builder;
import lombok.Getter;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.players.Player;

public final class PlayerProjectile extends AbstractProjectile<Player> {

    private final Damage damage;

    @Getter
    private final int hit;

    @Builder
    public PlayerProjectile(Player shooter,
                            PhysicalEntity target,
                            Damage damage,
                            int hit) {
        super(shooter, target, 2);
        this.hit = hit;
        this.damage = damage;
    }


    public Damage damage() {
        return damage;
    }
}
