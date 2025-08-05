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
        this.hit = shooter.accuracy();
        this.kungFu = kungFu;
    }


    private void handleSingleAttack() {
        target().findAbility(HurtAbility.class)
                .ifPresent(hurtAbility -> {
                    int exp = hurtAbility.attacked(shooter(), damage, hit);
                    kungFu.gainExp((Player) shooter(), exp);
                });
    }

    @Override
    protected void onReachTarget() {
        if (((Player)shooter()).isLeftRealm())
            return;
        ((Player)shooter()).assistantKungFu().ifPresentOrElse(assistantKungFu ->
                assistantKungFu.apply((Player) shooter(), target(), assistantKungFu.affectedCoordinates(target().coordinate(), direction()), damage),
                this::handleSingleAttack);
    }
}
