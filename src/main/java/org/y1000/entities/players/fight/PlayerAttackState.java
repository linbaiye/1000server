package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.AttackableEntity;
import org.y1000.entities.projectile.PlayerProjectile;
import org.y1000.entities.players.Damage;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.PlayerShootEvent;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.realm.Realm;

@Slf4j
public final class PlayerAttackState extends AbstractFightingState {

    private final State attackingState;
    private final AttackableEntity rangedTarget;
    private final Damage damage;
    private final int rangedHit;


    private PlayerAttackState(int totalMillis, State attackingState,
                              AttackableEntity target,
                              Damage damage,
                              int rangedHit) {
        super(totalMillis);
        this.attackingState = attackingState;
        this.rangedTarget = target;
        this.damage = damage;
        this.rangedHit = rangedHit;
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
            player.emitEvent(new PlayerShootEvent(new PlayerProjectile(player, rangedTarget, damage, rangedHit)));
        }
        player.attackKungFu().attackAgain(player);
    }

    @Override
    public Logger logger() {
        return log;
    }

    public static PlayerAttackState of(PlayerImpl player) {
        State state = player.attackKungFu().randomAttackState();
        int stateMillis = Math.min(player.getStateMillis(state), player.attackSpeed() * Realm.STEP_MILLIS);
        var rangedTarget = player.attackKungFu().isRanged() ? player.getFightingEntity() : null;
        return new PlayerAttackState(stateMillis, state, rangedTarget, player.damage(), player.hit());
    }
}
