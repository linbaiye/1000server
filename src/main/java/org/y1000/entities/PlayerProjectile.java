package org.y1000.entities;


import lombok.Builder;
import lombok.Getter;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.players.Player;

public final class PlayerProjectile {
    @Getter
    private final Player shooter;

    private final PhysicalEntity target;

    private final int flyingMillis;

    private final Damage damage;

    @Getter
    private final int hit;

    private int elapsed;

    @Builder
    public PlayerProjectile(Player shooter,
                            PhysicalEntity target,
                            int flyingMillis,
                            Damage damage,
                            int hit) {
        this.target = target;
        this.flyingMillis = flyingMillis;
        this.shooter = shooter;
        this.hit = hit;
        this.damage = damage;
    }

    public boolean update(int delta) {
        elapsed += delta;
        if (elapsed < flyingMillis) {
            return false;
        }
        target.attackedBy(this);
        return true;
    }

    public PhysicalEntity target() {
        return target;
    }

    public Damage damage() {
        return damage;
    }

    public int flyingMillis() {
        return flyingMillis;
    }
}
