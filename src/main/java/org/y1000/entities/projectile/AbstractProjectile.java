package org.y1000.entities.projectile;

import lombok.Getter;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.creatures.ViolentCreature;
import org.y1000.entities.players.Player;

public abstract class AbstractProjectile implements Projectile {

    @Getter
    private final ViolentCreature shooter;

    private final AttackableActiveEntity target;

    private final int flyingMillis;

    private int elapsed;

    private final int spriteId;

    private final Direction direction;


    public AbstractProjectile(ViolentCreature shooter,
                              AttackableActiveEntity target,
                              int spriteId) {
        this.spriteId = spriteId;
        int dist = shooter.coordinate().directDistance(target.coordinate());
        this.shooter = shooter;
        this.target = target;
        this.flyingMillis = dist * 30;
        this.direction = shooter.coordinate().computeDirection(target.coordinate());
    }

    @Override
    public Direction direction() {
        return direction;
    }

    protected abstract void onReachTarget();

    public boolean update(int delta) {
        elapsed += delta;
        if (elapsed < flyingMillis) {
            return false;
        }
        onReachTarget();
        return true;
    }

    public AttackableActiveEntity target() {
        return target;
    }

    public int flyingMillis() {
        return flyingMillis;
    }

    @Override
    public int projectileSpriteId() {
        return spriteId;
    }

    @Override
    public ViolentCreature shooter() {
        return shooter;
    }
}
