package org.y1000.entities.players.fight;

import org.slf4j.Logger;
import org.y1000.entities.Direction;
import org.y1000.entities.PhysicalEntity;
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


    Logger logger();

    default PlayerState rangedCooldownState(PlayerImpl player, PhysicalEntity target) {
        return PlayerRangedCooldownState.cooldown(player.cooldown(), target, 1);
    }

    default AbstractPlayerAttackState rangedAttackState(PlayerImpl player, PhysicalEntity target) {
        return PlayerRangedAttackState.rangedAttack(player, target);
    }

    default void fireAttack(PlayerImpl player, PhysicalEntity target, AbstractPlayerAttackState attackState) {
        Direction direction = player.coordinate().computeDirection(target.coordinate());
        player.changeDirection(direction);
        player.changeState(attackState);
        player.emitEvent(PlayerAttackEvent.of(player, target.id()));
    }


    default void attack(PlayerImpl player, PhysicalEntity target) {
        if (!target.attackable()) {
            player.changeState(PlayerStillState.chillOut(player));
            player.emitEvent(ChangeStateEvent.of(player));
            return;
        }
        boolean rangedAttack = player.attackKungFu().isRanged();
        if (player.cooldown() > 0) {
            var cdState = rangedAttack ? rangedCooldownState(player, target) :
                    new PlayerMeleeCooldownState(player.cooldown(), target);
            player.changeState(cdState);
            player.emitEvent(ChangeStateEvent.of(player));
            return;
        }
        var dist = player.coordinate().directDistance(target.coordinate());
        if (rangedAttack || dist <= 1) {
            var attackState = rangedAttack ? rangedAttackState(player, target) :
                    PlayerMeleeAttackState.meleeAttackState(player, target);
            fireAttack(player, target, attackState);;
        } else {
            player.changeState(PlayerMeleeAttackReadyState.prepareSwing(player, target));
            player.emitEvent(ChangeStateEvent.of(player));
        }
    }

    default void handleAttackEvent(PlayerImpl player, ClientAttackEvent event) {
        Optional<PhysicalEntity> insight = player.getRealm().findInsight(player, event.entityId());
        if (insight.isEmpty()) {
            player.emitEvent(new PlayerAttackEventResponse(player, event, false));
            return;
        }
        PhysicalEntity target = insight.get();
        if (!target.attackable()) {
            player.emitEvent(new PlayerAttackEventResponse(player, event, false));
            return;
        }
        player.emitEvent(new PlayerAttackEventResponse(player, event, true));
        Direction direction = player.coordinate().computeDirection(target.coordinate());
        player.changeDirection(direction);
        attack(player, target);
    }
}

