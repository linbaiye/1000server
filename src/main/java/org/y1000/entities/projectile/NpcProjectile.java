package org.y1000.entities.projectile;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.players.Damage;

public final class NpcProjectile extends AbstractProjectile {


    private final int accuracy;

    private final Damage damage;

    public NpcProjectile(ActiveEntity shooter,
                         ActiveEntity target,
                         String id,
                         int accuracy,
                         Damage damage) {
        super(shooter, target, id);
        this.accuracy = accuracy;
        this.damage = damage;
    }

    @Override
    public Damage damage() {
        return Damage.DEFAULT;
    }

    @Override
    public int hit() {
        return accuracy;
    }

    @Override
    protected void onReachTarget() {

    }
}
