package org.y1000.entities.players;

import org.y1000.entities.ActiveEntity;
import org.y1000.entities.players.event.PlayerLetFlyProjectileEvent;
import org.y1000.entities.players.event.PlayerSoundEvent;
import org.y1000.entities.projectile.PlayerProjectile;
import org.y1000.kungfu.attack.AbstractRangedKungFu;
import org.y1000.entities.players.event.PlayerChangeStateEvent;
import org.y1000.realm.Realm;

final class PlayerShootState extends AbstractPlayerAttackState {

    private boolean letFly;

    private final PlayerProjectile projectile;

    private final String sound;

    public PlayerShootState(PlayerImpl player, PlayerStateEnum stateEnum,
                            AttackAction action, PlayerProjectile projectile, String sound) {
        super(player, stateEnum, action.getMillis());
        this.projectile = projectile;
        this.sound = sound;
        letFly = false;
    }

    @Override
    public void update(int delta) {
        if (elapsedMillis() >= 200 && !letFly) {
            letFly = true;
            player().sendEvent(PlayerSoundEvent.toAll(player(), sound));
            player().sendEvent(PlayerLetFlyProjectileEvent.of(player(), projectile));
        }
        if (elapse(delta)) {
            player().changeState(PlayerStandState.fightStand(player()));
            player().sendEvent(PlayerChangeStateEvent.allVisible(player()));
        }
    }

    public static PlayerShootState aimTarget(PlayerImpl player,
                                             AbstractRangedKungFu kungFu,
                                             ActiveEntity target,
                                             String sprite,
                                             String sound) {
        PlayerProjectile projectile = new PlayerProjectile(player, target, kungFu, sprite);
        return new PlayerShootState(player, PlayerStateEnum.Attack, kungFu.computeAttackAction(), projectile, sound);
    }
}
