package org.y1000.entities.projectile;

import org.apache.commons.lang3.Validate;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.Direction;
import org.y1000.entities.HurtAbility;

public abstract class AbstractProjectile implements Projectile {
    private final ActiveEntity shooter;

    private final ActiveEntity target;
    private final int flyingMillis;

    private int elapsed;

    private final String sprite;

    private final Direction direction;


    public AbstractProjectile(ActiveEntity shooter,
                              ActiveEntity target,
                              String sprite) {
        Validate.isTrue(target.findAbility(HurtAbility.class).isPresent());
        this.sprite = sprite;
        int dist = shooter.coordinate().directDistance(target.coordinate());
        this.shooter = shooter;
        this.target = target;
        this.flyingMillis = dist * 30;
        this.direction = shooter.coordinate().directionTo(target.coordinate());
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

    public ActiveEntity target() {
        return target;
    }

    public int flyingMillis() {
        return flyingMillis;
    }

    @Override
    public String sprite() {
        return sprite;
    }

    @Override
    public ActiveEntity shooter() {
        return shooter;
    }
}
