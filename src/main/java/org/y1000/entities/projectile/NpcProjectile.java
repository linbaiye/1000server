package org.y1000.entities.projectile;

import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.creatures.npc.ViolentNpc;
import org.y1000.entities.players.Damage;

public final class NpcProjectile extends AbstractProjectile {

    public NpcProjectile(ViolentNpc shooter,
                         AttackableActiveEntity target, int id) {
        super(shooter, target, id);
    }

    @Override
    public Damage damage() {
        return shooter().damage();
    }

    @Override
    public int hit() {
        return shooter().hit();
    }

    @Override
    protected void onReachTarget() {
        target().attackedBy(shooter());
    }
}
