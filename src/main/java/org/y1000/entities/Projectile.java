package org.y1000.entities;


import lombok.Builder;
import lombok.Getter;
import org.y1000.entities.attribute.Damage;
import org.y1000.entities.creatures.ViolentCreature;
public final class Projectile {
    @Getter
    private final ViolentCreature shooter;

    private final PhysicalEntity target;

    private final int flyingMillis;

    private final Damage damage;

    @Getter
    private final int hit;

    private int elapsed;

    @Builder
    public Projectile(ViolentCreature shooter,
                      PhysicalEntity target, int flyingMillis) {
        this.damage = shooter.damage();
        this.target = target;
        this.flyingMillis = flyingMillis;
        this.shooter = shooter;
        this.hit = shooter.hit();
    }

    public boolean update(int delta) {
        elapsed += delta;
        if (elapsed < flyingMillis) {
            return false;
        }
        target.attackedBy(this);
        return true;
    }
}
