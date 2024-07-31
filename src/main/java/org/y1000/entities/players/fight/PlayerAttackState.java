package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.y1000.entities.AttackableActiveEntity;
import org.y1000.entities.projectile.PlayerProjectile;
import org.y1000.entities.players.Damage;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.PlayerShootEvent;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.realm.Realm;

@Slf4j
public final class PlayerAttackState extends AbstractFightingState {

    private final State attackingState;
    private final AttackableActiveEntity rangedTarget;
    private final Damage damage;
    private final int rangedHit;
    private final int spriteId;

    private PlayerAttackState(int totalMillis, State attackingState,
                              AttackableActiveEntity target,
                              Damage damage,
                              int rangedHit,
                              int spriteId) {
        super(totalMillis);
        this.attackingState = attackingState;
        this.rangedTarget = target;
        this.damage = damage;
        this.rangedHit = rangedHit;
        this.spriteId = spriteId;
    }

    @Override
    public State stateEnum() {
        return attackingState;
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
        State state = player.attackKungFu().randomAttackState();
        int stateMillis = Math.min(player.getStateMillis(state), player.attackSpeed() * Realm.STEP_MILLIS);
        return new PlayerAttackState(stateMillis, state, player.getFightingEntity(), player.damage(), player.hit(), spriteId);
    }

    public static PlayerAttackState melee(PlayerImpl player) {
        State state = player.attackKungFu().randomAttackState();
        int stateMillis = Math.min(player.getStateMillis(state), player.attackSpeed() * Realm.STEP_MILLIS);
        return new PlayerAttackState(stateMillis, state, null, null, 0, 0);
    }
}
