package org.y1000.entities.projectile;

import lombok.Builder;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.players.Player;

public final class PlayerProjectile extends AbstractProjectile {

    private final Damage damage;

    private final int hit;

    @Builder
    public PlayerProjectile(Player shooter,
                            PhysicalEntity target,
                            Damage damage,
                            int hit) {
        super(shooter, target, 2);
        this.damage = damage;
        this.hit = hit;
    }

    @Override
    public int hit() {
        return hit;
    }

    public Damage damage() {
        return damage;
    }
}
