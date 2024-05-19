package org.y1000.entities.players.fight;

import org.y1000.entities.Direction;
import org.y1000.entities.Entity;
import org.y1000.entities.creatures.CreatureState;
import org.y1000.entities.creatures.State;
import org.y1000.entities.creatures.event.ChangeStateEvent;
import org.y1000.entities.players.PlayerImpl;
import org.y1000.entities.players.PlayerState;
import org.y1000.entities.players.PlayerStillState;
import org.y1000.entities.players.event.PlayerAttackEvent;
import org.y1000.entities.players.event.PlayerAttackEventResponse;
import org.y1000.message.clientevent.ClientAttackEvent;

import java.util.Optional;

public interface AttackableState extends CreatureState<PlayerImpl> {

    default PlayerState remoteCooldownState(PlayerImpl player, Entity target) {
        return new PlayerMeleeCooldownState(player.getStateMillis(State.COOLDOWN), target);
    }

    default PlayerState remoteAttackState(PlayerImpl player, Entity target) {
        return PlayerBowAttackState.bow(player, target);
    }

    default void attack(PlayerImpl player, Entity target) {
        if (!target.attackable()) {
            player.changeState(PlayerStillState.chillOut(player));
            player.emitEvent(ChangeStateEvent.of(player));
            return;
        }
        Direction direction = player.coordinate().computeDirection(target.coordinate());
        player.changeDirection(direction);
        boolean rangedAttack = player.attackKungFu().isRanged();
        if (player.cooldown() > 0) {
            var cdState = rangedAttack ? remoteCooldownState(player, target) :
                    new PlayerMeleeCooldownState(player.getStateMillis(State.COOLDOWN), target);
            player.changeState(cdState);
            player.emitEvent(ChangeStateEvent.of(player));
            return;
        }
        var dist = player.coordinate().directDistance(target.coordinate());
        if (rangedAttack || dist <= 1) {
            player.cooldownAttack();
            State state = player.attackKungFu().randomAttackState();
            var attackState = rangedAttack ? remoteAttackState(player, target) :
                    PlayerMeleeAttackState.attack(target, state, player.getStateMillis(state));
            player.changeState(attackState);
            player.emitEvent(PlayerAttackEvent.of(player, target.id()));
        } else {
            player.changeState(PlayerMeleeAttackReadyState.prepareSwing(player, target));
            player.emitEvent(ChangeStateEvent.of(player));
        }
    }

    default void handleAttackEvent(PlayerImpl player, ClientAttackEvent event) {
        Optional<Entity> insight = player.getRealm().findInsight(player, event.entityId());
        if (insight.isEmpty()) {
            player.emitEvent(new PlayerAttackEventResponse(player, event, false));
            return;
        }
        Entity target = insight.get();
        if (!target.attackable()) {
            player.emitEvent(new PlayerAttackEventResponse(player, event, false));
            return;
        }
        player.emitEvent(new PlayerAttackEventResponse(player, event, true));
        attack(player, target);
    }
}

