package org.y1000.entities.projectile;

import org.y1000.entities.AttackableEntity;
import org.y1000.entities.creatures.npc.ViolentNpc;
import org.y1000.entities.players.Damage;
import org.y1000.entities.creatures.monster.AbstractMonster;

public final class NpcProjectile extends AbstractProjectile {

    public NpcProjectile(ViolentNpc shooter,
                         AttackableEntity target, int id) {
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
}
