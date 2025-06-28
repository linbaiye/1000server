package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.creatures.PlayerStateEnum;
import org.y1000.entities.projectile.PlayerProjectile;
import org.y1000.entities.players.Damage;
import org.y1000.entities.creatures.event.PlayerShootEvent;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.realm.Realm;

@Slf4j
public final class PlayerAttackState extends AbstractFightingState {

    private final PlayerStateEnum attackingPlayerStateEnum;
    private final AttackableActiveEntity rangedTarget;
    private final Damage damage;
    private final int rangedHit;
    private final int spriteId;

    private PlayerAttackState(int totalMillis, PlayerStateEnum attackingPlayerStateEnum,
                              AttackableActiveEntity target,
                              Damage damage,
                              int rangedHit,
                              int spriteId) {
        super(totalMillis);
        this.attackingPlayerStateEnum = attackingPlayerStateEnum;
        this.rangedTarget = target;
        this.damage = damage;
        this.rangedHit = rangedHit;
        this.spriteId = spriteId;
    }

    @Override
    public PlayerStateEnum stateEnum() {
        return attackingPlayerStateEnum;
    }


    @Override
    public void update(PlayerImpl player, int delta) {
        if (!elapse(delta)) {
            return;
        }
        if (rangedTarget != null) {
            player.emitEvent(new PlayerShootEvent(new PlayerProjectile(player, rangedTarget, damage, rangedHit, spriteId)));
        }
        player.attackKungFu().attackAgain(player);
    }

    @Override
    public Logger logger() {
        return log;
    }

    public static PlayerAttackState ranged(PlayerImpl player, int spriteId) {
        Validate.isTrue(player.attackKungFu().isRanged());
        Validate.notNull(player.getFightingEntity());
        PlayerStateEnum playerStateEnum = player.attackKungFu().randomAttackState();
        int stateMillis = Math.min(player.getStateMillis(playerStateEnum), player.attackSpeed() * Realm.STEP_MILLIS);
        return new PlayerAttackState(stateMillis, playerStateEnum, player.getFightingEntity(), player.damage(), player.hit(), spriteId);
    }

    public static PlayerAttackState melee(PlayerImpl player) {
        PlayerStateEnum playerStateEnum = player.attackKungFu().randomAttackState();
        int stateMillis = Math.min(player.getStateMillis(playerStateEnum), player.attackSpeed() * Realm.STEP_MILLIS);
        return new PlayerAttackState(stateMillis, playerStateEnum, null, null, 0, 0);
    }
}
