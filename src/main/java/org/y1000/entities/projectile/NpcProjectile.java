package org.y1000.entities.projectile;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.players.Damage;

public final class NpcProjectile extends AbstractProjectile {

    public NpcProjectile(ActiveEntity shooter,
                         ActiveEntity target, String id) {
        super(shooter, target, id);
    }

    @Override
    public Damage damage() {
        return Damage.DEFAULT;
    }

    @Override
    public int hit() {
        return 0;
    }

    @Override
    protected void onReachTarget() {
    }
}
