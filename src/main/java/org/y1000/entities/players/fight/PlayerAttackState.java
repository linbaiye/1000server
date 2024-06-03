package org.y1000.entities.players.fight;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.Projectile;
import org.y1000.entities.creatures.AbstractCreateState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.CreatureShootEvent;
import org.y1000.entities.players.MovableState;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;
import org.y1000.entities.players.PlayerStillState;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.entities.players.event.PlayerCooldownEvent;

@Slf4j
public final class PlayerAttackState extends AbstractCreateState<PlayerImpl> implements PlayerState, MovableState {

    private final State attackingState;

    public PlayerAttackState(int totalMillis, State attackingState) {
        super(totalMillis);
        this.attackingState = attackingState;
    }

    @Override
    public State stateEnum() {
        return attackingState;
    }

    @Override
    public void update(PlayerImpl player, int delta) {
        if (elapsedMillis() == 0) {
            if (player.isFighting()) {
                if (!player.attackKungFu().useResources(player)) {
                    player.changeState(PlayerWaitResourceState.of(player));
                    player.emitEvent(PlayerCooldownEvent.of(player));
                    return;
                }
                player.cooldownAttack();
                player.emitEvent(new PlayerAttackEvent(player, attackingState));
            } else {
                player.changeState(PlayerStillState.chillOut(player));
                player.emitEvent(PlayerCooldownEvent.of(player));
            }
        }
        if (!elapse(delta)) {
            return;
        }
        if (player.attackKungFu().isRanged() && player.isFighting()) {
            int dist = player.coordinate().directDistance(player.getFightingEntity().coordinate());
            player.emitEvent(new CreatureShootEvent(new Projectile(player, player.getFightingEntity(), dist * 30)));
        }
        if (player.cooldown() > 0) {
            player.changeState(new PlayerCooldownState(player.cooldown()));
        } else {
            player.changeState(PlayerAttackState.of(player));
        }
    }

    @Override
    public PlayerState stateForStuckMoving(PlayerImpl player) {
        return null;
    }

    @Override
    public PlayerState stateForMove(PlayerImpl player, Direction direction) {
        return null;
    }

    @Override
    public Logger logger() {
        return log;
    }

    public static PlayerAttackState of(PlayerImpl player) {
        State state = player.attackKungFu().randomAttackState();
        return new PlayerAttackState(player.getStateMillis(state), state);
    }
}
