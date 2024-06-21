package org.y1000.entities.projectile;

import lombok.Getter;
import org.y1000.entities.PhysicalEntity;
import org.y1000.entities.creatures.ViolentCreature;

public abstract class AbstractProjectile<C extends ViolentCreature> {

    @Getter
    private final C shooter;

    private final PhysicalEntity target;

    private final int flyingMillis;

    private int elapsed;

    private final int spriteId;

    public AbstractProjectile(C shooter,
                              PhysicalEntity target,
                              int spriteId) {
        this.spriteId = spriteId;
        int dist = shooter.coordinate().directDistance(target.coordinate());
        this.shooter = shooter;
        this.target = target;
        this.flyingMillis = dist * 30;
    }

    public boolean update(int delta) {
        elapsed += delta;
        if (elapsed < flyingMillis) {
            return false;
        }
        target.attackedBy(shooter);
        return true;
    }

    public PhysicalEntity target() {
        return target;
    }

    public int flyingMillis() {
        return flyingMillis;
    }

    public int spriteId() {
        return spriteId;
    }

}
