package org.y1000.entities.projectile;

import lombok.Builder;
import org.y1000.entities.ActiveEntity;
import org.y1000.entities.HurtAbility;
import org.y1000.entities.players.Damage;
import org.y1000.entities.players.Player;
import org.y1000.kungfu.attack.AbstractRangedKungFu;

public final class PlayerProjectile extends AbstractProjectile {

    private final Damage damage;

    private final int hit;

    private final AbstractRangedKungFu kungFu;

    @Builder
    public PlayerProjectile(Player shooter,
                            ActiveEntity target,
                            AbstractRangedKungFu kungFu,
                            String sprite) {
        super(shooter, target, sprite);
        this.damage = kungFu.damage();
        this.hit = shooter.hit();
        this.kungFu = kungFu;
    }

    @Override
    public int hit() {
        return hit;
    }

    public Damage damage() {
        return damage;
    }

    @Override
    protected void onReachTarget() {
        target().findAbility(HurtAbility.class)
                .ifPresent(hurtAbility -> {
                    int exp = hurtAbility.attacked(shooter(), damage, hit);
                    kungFu.gainExp((Player) shooter(), exp);
                });
    }
}
